package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.mapper.FilmDirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.FilmGenreRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbc;
    private final GenreStorage genreStorage;
    @Autowired
    private FilmRowMapper filmRowMapper;

    public FilmDbStorage(@Autowired NamedParameterJdbcTemplate jdbc,
                         @Autowired GenreStorage genreStorage) {
        this.jdbc = jdbc;
        this.genreStorage = genreStorage;
    }

    // Запрос для заполнения информации о фильме
    private static final String SQL_INSERT_FILM = """
            INSERT INTO films (name, description, releasedate, len_min, mpa_id)
            VALUES ( :name, :description, :releasedate, :len_min, :mpa_id)
            """;
    private static final String SQL_UPDATE_DIRECTORS = """
            INSERT INTO films_directors (film_id, director_id) VALUES (:film_id, :director_id)""";

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

        // добавляем жанры Фильма
        genreStorage.saveFilmGeres(newFilm);

        // Добавляем режиссеров фильма, если определены
        if (!newFilm.getDirectors().isEmpty()) {
            SqlParameterSource[] batchDirectors = newFilm.getDirectors().stream()
                    .map(director -> new MapSqlParameterSource()
                            .addValue("film_id", filmId)
                            .addValue("director_id", director.getId()))
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SQL_UPDATE_DIRECTORS, batchDirectors);
        }

  // возвращаем объект прочитанный из базы
        return getFilmById(filmId).orElseThrow(() ->
                new InternalServerException("Ошибка при добавлении фильма."));
    }

    private static final String SQL_FIND_FILM_BY_ID = """
                SELECT f.*, mpa.name as mpa_name
                FROM films AS f INNER JOIN mpa ON f.MPA_ID = mpa.ID
                WHERE f.id = :id;
            """;

    /**
     * Поиск фильма по идентификатору
     *
     * @param filmId - идентификатор фильма
     * @return - объект описания фильма
     */
    @Override
    public Optional<Film> getFilmById(Integer filmId) {
        try {
            Film film = jdbc.queryForObject(SQL_FIND_FILM_BY_ID,
                    new MapSqlParameterSource()

                            .addValue("id", filmId),
                    new FilmRowMapper());

            // Загружаем список жанров к фильму
            for (Genre genre : genreStorage.findGenresByFilmId(filmId)) {
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
            SELECT f.*, f.id AS film_id, mpa.name AS mpa_name, popular.count_film
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

        // сохраняем жанры фильма
        genreStorage.saveFilmGeres(updFilm);

        // Удаляем старых режиссеров, которые были определены для фильма
        jdbc.update("DELETE FROM films_directors WHERE film_id = :filmId",
                new MapSqlParameterSource().addValue("filmId", filmId));

        // Добавляем режиссеров фильма, если определены новые
        if (!updFilm.getDirectors().isEmpty()) {
            SqlParameterSource[] batchDirectors = updFilm.getDirectors().stream()
                    .map(director -> new MapSqlParameterSource()
                            .addValue("film_id", updFilm.getId())
                            .addValue("director_id", director.getId()))
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SQL_UPDATE_DIRECTORS, batchDirectors);
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
        jdbc.update("DELETE FROM films_directors", new MapSqlParameterSource()
                .addValue("table", "films_directors"));
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
            Collection<FilmGenre> filmsGenres;
            filmsGenres = genreStorage.findAllFilmWhithGenres();

            // пополням фильмы сведениями о жанрах
            for (FilmGenre filmGenre : filmsGenres) {
                int filmId = filmGenre.getFilmId();
                if (filmsMap.keySet().contains(filmId)) {
                    filmsMap.get(filmId).addGenre(filmGenre.getGenre());
                }
            }

            // Загружаем из базы данных всех режиссеров
            List<FilmDirector> filmsDirectors = jdbc.query(
                    "SELECT fd.*, d.name AS director_name FROM films_directors AS fd INNER JOIN directors AS d ON fd.DIRECTOR_ID = d.ID",
                    new FilmDirectorRowMapper());

            // Пополняем фильмы сведениями о режиссерах
            for (FilmDirector filmDirector : filmsDirectors) {
                int filmId = filmDirector.getFilmId();
                if (filmsMap.containsKey(filmId)) {
                    Director director = new Director();
                    director.setId(filmDirector.getDirectorId());
                    director.setName(filmDirector.getDirectorName());
                    filmsMap.get(filmId).addDirector(director);
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

    public List<Film> findFilmsByDirector(int directorId, String sortBy) {
        String sql;
        if ("likes".equalsIgnoreCase(sortBy)) {
            sql = "SELECT f.* FROM films f " +
                    "JOIN films_directors fd ON f.id = fd.film_id " +
                    "JOIN likes l ON f.id = l.film_id " +
                    "WHERE fd.director_id = :directorId " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.id) DESC";
        } else {
            sql = "SELECT f.* FROM films f " +
                    "JOIN films_directors fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = :directorId " +
                    "ORDER BY f.release_date";
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("directorId", directorId);

        return jdbc.query(sql, params, new FilmRowMapper());
    }

}
