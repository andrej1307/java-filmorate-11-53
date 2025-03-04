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
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Autowired
    private GenreStorage genreStorage;

    /**
     * Запросы для заполнения информации о фильме
     */
    private static final String SQL_INSERT_FILM = """
            INSERT INTO films (name, description, releasedate, len_min, mpa_id)
            VALUES ( :name, :description, :releasedate, :len_min, :mpa_id)
            """;
    private static final String SQL_UPDATE_GENRES = """
            MERGE INTO films_genres (film_id, genre_id) 
             VALUES (:film_id, :genre_id)
            """;
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
            try {
                SqlParameterSource[] batch = newFilm.getGenres().stream()
                        .map(genre -> new MapSqlParameterSource()
                                .addValue("film_id", filmId)
                                .addValue("genre_id", genre.getId()))
                        .toArray(SqlParameterSource[]::new);
                jdbc.batchUpdate(SQL_UPDATE_GENRES, batch);
            } catch (DataAccessException ignored) {
                throw new InternalServerException("Ошибка при заполнении жанров фильма.");
            }
        }

        return getFilmById(filmId).orElseThrow(() ->
                new InternalServerException("Ошибка при добавлении фильма."));
    }

    private static final String SQL_FIND_FILM_BY_ID = """
            SELECT f.*, mpa.name as mpa_name, f.id AS film_id, fg.genre_id, g.name AS genre_name
            FROM (films AS f INNER JOIN mpa ON f.MPA_ID = mpa.ID)
                LEFT JOIN (films_genres AS fg INNER JOIN genres AS g ON fg.GENRE_ID = g.ID)\s
                ON fg.film_id = f.id
            WHERE f.id = :id
            """;
    /**
     * Поиск фильма по идентификатору
     *
     * @param id - идентификатор фильма
     * @return - объект описания фильма
     */
    @Override
    public Optional<Film> getFilmById(Integer id) {
        try {
            Film film = jdbc.queryForObject(SQL_FIND_FILM_BY_ID,
                    new MapSqlParameterSource()
                            .addValue("id", id),
                            new FilmRowMapper());

            Integer filmId = film.getId();
            Collection<Genre> genres = genreStorage.findGenresByFilmId(filmId);
            for (Genre genre : genres) {
                film.addGenre(genre);
            }
            return Optional.ofNullable(film);

        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }


    private static final String SQL_FIND_ALL_FILMS = """
            SELECT f.*, mpa.name as mpa_name FROM films AS f
            INNER JOIN mpa ON f.mpa_id = mpa.id
            """;
    /**
     * Поиск всех фильмов
     *
     * @return - список фильмов
     */
    @Override
    public Collection<Film> findAllFilms() {
        return findFilmsByQuery(SQL_FIND_ALL_FILMS);
    }

    private static final String SQL_FIND_POPULAR_FILMS = """
            SELECT f.*, mpa.name AS mpa_name, popular.count_film
            FROM (films AS f INNER JOIN mpa ON f.MPA_ID = mpa.ID)
                 LEFT OUTER JOIN
                 (SELECT film_id, count(film_id) as count_film
                 FROM LIKES GROUP BY film_id) AS popular
                 ON f.id = popular.film_id
            ORDER BY popular.count_film DESC
            """;
    /**
     * Поиск популярных фильмов
     *
     * @param count - количество фильмов в итоговом списке
     * @return - список самых популярных фильмов
     */
    @Override
    public Collection<Film> findPopularFilms(int count) {
        if (count > 0) {
            return findFilmsByQuery(SQL_FIND_POPULAR_FILMS + " LIMIT " + count);
        }
        return findFilmsByQuery(SQL_FIND_POPULAR_FILMS);
    }

    private static final String SQL_UPDATE_FILM = """
            UPDATE films SET name = :name, description = :description, 
            releasedate = :releasedate, len_min = :len_min, mpa_id = :mpa_id  WHERE id = :id
            """;
    /**
     * Обновление сведений о фильме
     *
     * @param updFilm - объект c информацией для обновления. id должен быть определен
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
        if (!updFilm.getGenres().isEmpty()) {
            SqlParameterSource[] batch = updFilm.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("film_id", updFilm.getId())
                            .addValue("genre_id", genre.getId()))
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SQL_UPDATE_GENRES, batch);
        }
    }

    private static final String SQL_ADD_LIKE = "MERGE INTO likes (user_id, film_id) VALUES (:userId, :filmId)";
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

    private static final String SQL_REMOVE_LIKE = "DELETE FROM likes WHERE user_id = :userId AND film_id = :filmId";
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

    /**
     * Удаление всех фильмов
     */
    @Override
    public void removeAllFilms() {
        jdbc.update("DELETE FROM likes", new MapSqlParameterSource()
                .addValue("table", "likes"));
        jdbc.update("DELETE FROM films_genres", new MapSqlParameterSource()
                .addValue("table", "films_genres"));
        jdbc.update("DELETE FROM films", new MapSqlParameterSource()
                .addValue("table", "films"));
    }

    /**
     * Метод поиска фильмов заполнения их соответствующими жанрами
     *
     * @param sqlQueryFilms - строка SQL запроса для выборки всех полей объекта Film
     * @return - коллекция фильмов.
     */
    private Collection<Film> findFilmsByQuery(String sqlQueryFilms) {
        try {
            // Загружаем из базы данных информацию о фильмах
            Map<Integer, Film> filmsMap;
            filmsMap = jdbc.query(sqlQueryFilms,
                    new ResultSetExtractor<Map<Integer, Film>>() {
                        @Override
                        public Map<Integer, Film> extractData(ResultSet rs)
                                throws SQLException, DataAccessException {
                            Map<Integer, Film> fMap = new LinkedHashMap<>();
                            while (rs.next()) {
                                Film film = new FilmRowMapper().mapRow(rs, 1);
                            /*    Integer mpaId = rs.getInt("mpa_id");
                                if (mpaId != 0) {
                                    Mpa mpa = new Mpa();
                                    mpa.setId(mpaId);
                                    mpa.setName(rs.getString("mpa_name"));
                                    film.setMpa(mpa);
                                }
                             */
                                fMap.put(film.getId(), film);
                            }
                            return fMap;
                        }
                    });
            // Если ничего не нашли, то возвращаем пустой список
            if (filmsMap.isEmpty()) {
                return List.of();
            }

            // Загружаем из базы данных все ссылки на жанры
            List<FilmGenre> filmsGenres;
            filmsGenres = jdbc.query(
                    "SELECT fg.*, g.name AS genre_name FROM films_genres AS fg INNER JOIN genres AS g ON fg.GENRE_ID = g.ID",
                    new FilmGenreRowMapper());

            // пополням фильмы сведениями о жанрах
            for (FilmGenre filmGenre : filmsGenres) {
                int filmId = filmGenre.getFilmId();
                if (filmsMap.keySet().contains(filmId)) {
                    filmsMap.get(filmId).addGenre(filmGenre.getGenre());
                }
            }
            return filmsMap.values();

        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    private static final String SQL_FIND_COMMON_FILMS_FORMATTER = """
            SELECT f1.*, common.count_likes AS popular
            FROM (SELECT f.*, mpa.name as mpa_name FROM films AS f INNER JOIN mpa ON f.mpa_id = mpa.id) AS f1
            INNER JOIN (SELECT t1.*, t2.count_likes
                        FROM (SELECT film_id, COUNT(film_id) as count_film
                              FROM likes WHERE (user_id = %d OR user_id = %d)
                        GROUP BY film_id) AS t1 -- таблица всех идентификаторов фильмов с лайками обоих пользователей 
            INNER JOIN (SELECT  film_id, count(film_id) as count_likes
                        FROM LIKES GROUP BY film_id) AS t2 -- таблица популярности фильмов
                        ON t1.film_id = t2.film_id
                        WHERE count_film = 2) AS common -- таблица общих фильмов
            ON f1.id = common.film_id
            ORDER BY popular DESC;
            """;
    /**
     * Поиск общих фильмов у пользователей
     *
     * @param userId1 - идентификатор пользователя
     * @param userId2 - идентификатор пользователя
     * @return - список фильмов
     */
    @Override
    public Collection<Film> findCommonFilms(Integer userId1, Integer userId2) {
        String query = String.format(SQL_FIND_COMMON_FILMS_FORMATTER, userId1, userId2);
        return findFilmsByQuery(query);
    }
}
