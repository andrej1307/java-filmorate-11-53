package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.Types;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbc;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public FilmDbStorage(@Autowired NamedParameterJdbcTemplate jdbc,
                         @Autowired GenreStorage genreStorage,
                         @Autowired DirectorStorage directorStorage) {
        this.jdbc = jdbc;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
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
        directorStorage.saveFilmDirectors(newFilm);

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
            // Загружаем список директоров к фильму
            for (Director director : directorStorage.findDirectorByFilmId(filmId)) {
                film.addDirector(director);
            }
            return Optional.ofNullable(film);

        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    private static final String SQL_FIND_FILMS_BY_IDS = """
            SELECT f.*, mpa.name as mpa_name FROM (films AS f
            INNER JOIN mpa ON f.mpa_id = mpa.id)
            WHERE f.id IN (:films_ids)
            """;

    /**
     * Поиск фильмов по идентификаторам
     *
     * @param filmsIds - список идентификаторов
     *
     * @return - список фильмов с соответствющими идентификаторами.
     * Примечание:
     * последовательность фильмов в выходном списке не сохраняется
     */
    @Override
    public Collection<Film> findFilmsByIds(List<Integer> filmsIds) {
        // Загружаем из базы данных информацию о фильмах
        try {
            List<Film> films = jdbc.query(SQL_FIND_FILMS_BY_IDS,
                    new MapSqlParameterSource()
                            .addValue("films_ids", filmsIds),
                    new FilmRowMapper());
            return updateFilmsEnviroment(films);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
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
        // Загружаем из базы данных информацию о фильмах
        try {
            List<Film> films = jdbc.query(SQL_FIND_ALL_FILMS, new FilmRowMapper());
            return updateFilmsEnviroment(films);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
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
     * @return - список самых популярных фильмов
     */
    @Override
    public Collection<Film> findPopularFilms() {
        List<Film> films;
        try {
            films = jdbc.query(SQL_FIND_POPULAR_FILMS, new FilmRowMapper());
            return updateFilmsEnviroment(films);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
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

        // Добавляем режиссеров фильма, если определены новые
        directorStorage.saveFilmDirectors(updFilm);
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
        jdbc.update("DELETE FROM feedbacks", new MapSqlParameterSource()
                .addValue("table", "feedbacks"));
        jdbc.update("DELETE FROM reviews", new MapSqlParameterSource()
                .addValue("table", "reviews"));
        jdbc.update("DELETE FROM films_genres", new MapSqlParameterSource()
                .addValue("table", "films_genres"));
        jdbc.update("DELETE FROM films_directors", new MapSqlParameterSource()
                .addValue("table", "films_directors"));
        jdbc.update("DELETE FROM films", new MapSqlParameterSource()
                .addValue("table", "films"));
    }

    /**
     * Заполнение списка фильмов
     * сопутствующими объектами: жанрами, режиссерами, и т.д.
     *
     * @return - коллекция фильмов.
     */
    private Collection<Film> updateFilmsEnviroment(List<Film> films) {
        try {
            // Преобразуем список в Map с идентификаторами в качестве ключа
            LinkedHashMap<Integer, Film> filmsMap = new LinkedHashMap<>();
            for (int i = 0; i < films.size(); i++) {
                filmsMap.put(films.get(i).getId(), films.get(i));
            }

            // пополням фильмы сведениями о жанрах
            for (FilmGenre filmGenre : genreStorage.findAllFilmWhithGenres()) {
                int filmId = filmGenre.getFilmId();
                if (filmsMap.keySet().contains(filmId)) {
                    filmsMap.get(filmId).addGenre(filmGenre.getGenre());
                }
            }

            // Пополняем фильмы сведениями о режиссерах
            for (FilmDirector filmDirector : directorStorage.findAllFilmDirector()) {
                int filmId = filmDirector.getFilmId();
                if (filmsMap.keySet().contains(filmId)) {
                    filmsMap.get(filmId).addDirector(filmDirector.getDirector());
                }
            }
            return filmsMap.values();

        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    private static final String SQL_FIND_COMMON_FILMS = """
            SELECT f1.*, common.count_likes AS popular
            FROM (SELECT f.*, mpa.name as mpa_name FROM films AS f INNER JOIN mpa ON f.mpa_id = mpa.id) AS f1
            INNER JOIN (SELECT t1.*, t2.count_likes
                        FROM (SELECT film_id, COUNT(film_id) as count_film
                              FROM likes WHERE (user_id = :id1 OR user_id = :id2)
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
        try {
            // Загружаем из базы данных информацию о фильмах
            List<Film> films = jdbc.query(SQL_FIND_COMMON_FILMS,
                    new MapSqlParameterSource()
                            .addValue("id1", userId1)
                            .addValue("id2", userId2),
                    new FilmRowMapper());
            return updateFilmsEnviroment(films);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }
}
