package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (name, description, releasedate, len_min, mpa_id)" +
                    "VALUES ( :name, :description, :releasedate, :len_min, :mpa_id)";
    private final String SQL_UPDATE_GENRES = "MERGE INTO films_genres (film_id, genre_id) " +
            "VALUES (:film_id, :genre_id)";
    private static final String SQL_FIND_FILM_BY_ID = "SELECT f.*, mpa.name as mpa_name, fg.genre_id, g.name AS genre_name\n" +
            "            FROM (films AS f INNER JOIN mpa ON f.MPA_ID = mpa.ID)\n" +
            "                LEFT JOIN (films_genres AS fg INNER JOIN genres AS g ON fg.GENRE_ID = g.ID) ON fg.film_id = f.id\n" +
            "            WHERE f.id = :id";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET name = :name, description = :description, " +
            "releasedate = :releasedate, len_min = :len_min, mpa_id = :mpa_id  WHERE id = :id";
    private static final String SQL_ADD_LIKE = "MERGE INTO likes (user_id, film_id) VALUES (:userId, :filmId)";
    private static final String SQL_REMOVE_LIKE = "DELETE FROM likes WHERE user_id = :userId AND film_id = :filmId";

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

/**
 * Добавление информации о фильме
 *
 * @param newFilm - объект для добавления
 * @return - подтвержденный объект
 */
@Override
public Film addNewFilm(Film newFilm) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

    // сохраняем информацию о фильме в базу данных
    try {
        jdbc.update(SQL_INSERT_FILM,
                new MapSqlParameterSource()
                        .addValue("name", newFilm.getName())
                        .addValue("description", newFilm.getDescription())
                        .addValue("releasedate", newFilm.getReleaseDate(), Types.DATE)
                        .addValue("len_min", newFilm.getDuration())
                        .addValue("mpa_id", newFilm.getMpa().getId()),
                generatedKeyHolder
        );
    } catch (DataAccessException e) {
        throw new NotFoundException("Получены недопустимые параметры запроса: " +
                e.getMessage());
    }

    // получаем идентификатор фильма
    final Integer filmId = generatedKeyHolder.getKey().intValue();
    newFilm.setId(filmId);

    // добавляем жанры Фильма Если определены
    if (!newFilm.getGenres().isEmpty()) {
        SqlParameterSource[] batch = newFilm.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("film_id", filmId)
                        .addValue("genre_id", genre.getId()))
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate(SQL_UPDATE_GENRES, batch);
    }

    return getFilmById(filmId).orElseThrow(() ->
            new InternalServerException("Ошибка при добавлении фильма."));
}

/**
 * Поиск фильма по идентификатору
 *
 * @param id - идентификатор фильма
 * @return - объект описания фильма
 */
@Override
public Optional<Film> getFilmById(Integer id) {
    try {
        Film film = jdbc.query(SQL_FIND_FILM_BY_ID,
                new MapSqlParameterSource()
                        .addValue("id", id),
                new ResultSetExtractor<Film>() {
                    @Override
                    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
                        rs.next();
                        Film filmRs = new FilmRowMapper().mapRow(rs, 1);
                        Integer mpaId = rs.getInt("mpa_id");
                        if (mpaId != null) {
                            Mpa mpa = new Mpa();
                            mpa.setId(mpaId);
                            mpa.setName(rs.getString("mpa_name"));
                            filmRs.setMpa(mpa);
                        }
                        do {
                            Integer genreId = rs.getInt("genre_id");
                            if (genreId != 0) {
                                Genre genre = new Genre();
                                genre.setId(genreId);
                                genre.setName(rs.getString("genre_name"));
                                filmRs.addGenre(genre);
                            }
                        } while (rs.next());
                        return filmRs;
                    }
                }
        );
        return Optional.ofNullable(film);

    } catch (DataAccessException ignored) {
        return Optional.empty();
    }
}

/**
 * Поиск всех фильмов
 *
 * @return - список фильмов
 */
@Override
public Collection<Film> findAllFilms() {
    try {
        // Загружаем из базы данных все фильмы
        Map<Integer, Film> filmsMap = new HashMap<>();
        filmsMap = jdbc.query("SELECT f.*, mpa.name as mpa_name FROM films AS f INNER JOIN mpa ON f.mpa_id = mpa.id",
                new ResultSetExtractor<Map<Integer, Film>>() {
                    @Override
                    public Map<Integer, Film> extractData(ResultSet rs)
                            throws SQLException, DataAccessException {
                        Map<Integer, Film> fMap = new HashMap<>();
                        while (rs.next()) {
                            Film film = new FilmRowMapper().mapRow(rs, 1);
                            Integer mpaId = rs.getInt("mpa_id");
                            if (mpaId != 0) {
                                Mpa mpa = new Mpa();
                                mpa.setId(mpaId);
                                mpa.setName(rs.getString("mpa_name"));
                                film.setMpa(mpa);
                            }
                            fMap.put(film.getId(), film);
                        }
                        return fMap;
                    }
                });

        // загружаем из базы данных все ссылки на жанры
        List<FilmGenre> filmsGenres;
        filmsGenres = jdbc.query(
                "SELECT fg.*, g.name AS genre_name FROM films_genres AS fg INNER JOIN genres AS g ON fg.GENRE_ID = g.ID",
                new FilmGenreRowMapper());

        // пополням фильмы сведениями о жанрах
        for (FilmGenre filmGenre : filmsGenres) {
            Genre genre = new Genre();
            int filmId = filmGenre.getFilmId();
            genre.setId(filmGenre.getGenreId());
            genre.setName(filmGenre.getGenreName());
            filmsMap.get(filmId).addGenre(genre);
        }

        return filmsMap.values();

    } catch (EmptyResultDataAccessException ignored) {
        return List.of();
    }

}

/**
 * Поиск популярных фильмов
 *
 * @param count
 * @return
 */
@Override
public Collection<Film> findPopularFilms(int count) {
    String sql = "SELECT f.*, mpa.name AS mpa_name, popular.count_film\n" +
            "FROM (films AS f INNER JOIN mpa ON f.MPA_ID = mpa.ID\n)" +
            "    LEFT OUTER JOIN\n" +
            "    (SELECT film_id, count(film_id) as count_film\n" +
            "     FROM LIKES GROUP BY film_id) AS popular\n" +
            "        ON f.id = popular.film_id\n" +
            "ORDER BY popular.count_film DESC\n" +
            "LIMIT :count";

    Map<Integer, Film> popularFilms;
    try {
        popularFilms = jdbc.query(sql, new MapSqlParameterSource()
                        .addValue("count", count),
                new ResultSetExtractor<Map<Integer, Film>>() {
                    @Override
                    public Map<Integer, Film> extractData(ResultSet rs)
                            throws SQLException, DataAccessException {
                        Map<Integer, Film> mapFilm = new LinkedHashMap<>();
                        while (rs.next()) {
                            Film film = new FilmRowMapper().mapRow(rs, 1);
                            Integer mpaId = rs.getInt("mpa_id");
                            if (mpaId != 0) {
                                Mpa mpa = new Mpa();
                                mpa.setId(mpaId);
                                mpa.setName(rs.getString("mpa_name"));
                                film.setMpa(mpa);
                            }
                            mapFilm.put(film.getId(), film);
                        }
                        return mapFilm;
                    }
                });

        // загружаем из базы данных соответствующие ссылки на жанры
        List<Integer> ids = new ArrayList<>(popularFilms.keySet());
        List<FilmGenre> filmsGenres;
        filmsGenres = jdbc.query(
                "SELECT fg.*, " +
                        "g.name AS genre_name " +
                        "FROM films_genres AS fg INNER JOIN genres AS g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id IN (:values)",
                new MapSqlParameterSource()
                        .addValue("values", ids),
                new FilmGenreRowMapper());

        // пополням фильмы сведениями о жанрах
        for (FilmGenre filmGenre : filmsGenres) {
            Genre genre = new Genre();
            int filmId = filmGenre.getFilmId();
            genre.setId(filmGenre.getGenreId());
            genre.setName(filmGenre.getGenreName());
            popularFilms.get(filmId).addGenre(genre);
        }
        return popularFilms.values();

    } catch (EmptyResultDataAccessException ignored) {
        return List.of();
    }
}

/**
 * Обновление сведений о фильме
 *
 * @param updFilm - обновленный объект
 */
@Override
public void updateFilm(Film updFilm) {
    // задаем параметры SQL запоса
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("name", updFilm.getName());
    params.addValue("description", updFilm.getDescription());
    params.addValue("releasedate", updFilm.getReleaseDate(), Types.DATE);
    params.addValue("len_min", updFilm.getDuration());
    params.addValue("mpa_id", updFilm.getMpa().getId());
    params.addValue("id", updFilm.getId());

    // обновляем информацию о фильме
    int rowsUpdated = jdbc.update(SQL_UPDATE_FILM, params);
    if (rowsUpdated == 0) {
        throw new InternalServerException("Не удалось обновить информацию о фильие");
    }

    // Удаляем все жанры которые были определены для фильма
    int filmId = updFilm.getId();
    jdbc.update("DELETE FROM films_genres WHERE film_id = :filmId",
            new MapSqlParameterSource()
                    .addValue("filmId", filmId));

    // добавляем жанры Фильма если определены новые
    if (updFilm.getGenres().size() > 0) {
        SqlParameterSource[] batch = updFilm.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("film_id", updFilm.getId())
                        .addValue("genre_id", genre.getId()))
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate(SQL_UPDATE_GENRES, batch);
    }
}

/**
 * Добавление "лайка" к фильму.
 *
 * @param filmId - идентификатор фильма
 * @param userId - идентификатор пользователя
 * @return - число "лайков"
 */
@Override
public Integer addNewLike(Integer filmId, Integer userId) {
    int rowsUpdated = jdbc.update(SQL_ADD_LIKE, new MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("filmId", filmId)
    );
    if (rowsUpdated == 0) {
        throw new InternalServerException("Не удалось обновить данные");
    }
    return getFilmRank(filmId);
}

/**
 * Удаление "лайка" у фильма.
 *
 * @param filmId - идентификатор фильма
 * @param userId - идентификатор пользователя
 * @return - число "лайков"
 */
@Override
public Integer removeLike(Integer filmId, Integer userId) {
    jdbc.update(SQL_REMOVE_LIKE, new MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("filmId", filmId)
    );
    return getFilmRank(filmId);
}

/**
 * Подсчет "лайков" фильма.
 *
 * @param filmId - идентификатор фильма
 * @return - число "лайков"
 */
@Override
public Integer getFilmRank(Integer filmId) {
    try {
        return jdbc.queryForObject("SELECT count(film_id) FROM likes WHERE film_id = :filmId",
                new MapSqlParameterSource()
                        .addValue("filmId", filmId),
                Integer.class);
    } catch (EmptyResultDataAccessException ignored) {
        throw new NotFoundException("Информация о популярности фильма не найдена. id:" + filmId);
    }
}

@Override
public void removeAllFilms() {
    jdbc.update("DELETE FROM likes", new MapSqlParameterSource()
            .addValue("table", "likes"));
    jdbc.update("DELETE FROM films_genres", new MapSqlParameterSource()
            .addValue("table", "films_genres"));
    jdbc.update("DELETE FROM films", new MapSqlParameterSource()
            .addValue("table", "films"));
}
}
