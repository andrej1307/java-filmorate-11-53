package ru.yandex.practicum.filmorate.storage.bdadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDbStorage implements AdminStorage {

    /**
     * Запросы удаления фильмов
     */
    private static final String SQL_REMOVE_LIKE_FILM_BY_ID = "DELETE FROM likes WHERE film_id = :filmId";
    private static final String SQL_REMOVE_GENRES_FILM_BY_ID = "DELETE FROM films_genres WHERE film_id = :filmId";
    private static final String SQL_REMOVE_DIRECTORS_FILM_BY_ID = "DELETE FROM films_directors WHERE film_id = :filmId";
    private static final String SQL_REMOVE_REVIEWS_FILM_BY_ID = "DELETE FROM reviews WHERE film_id = :filmId";
    private static final String SQL_REMOVE_FILM_BY_ID = "DELETE FROM films WHERE id = :filmId";
    /**
     * Запросы удаления пользователей
     */
    private static final String SQL_REMOVE_LIKE_USER_BY_ID = "DELETE FROM likes WHERE USER_ID = :userId";
    private static final String SQL_REMOVE_REVIEWS_USER_BY_ID = "DELETE FROM reviews WHERE USER_ID = :userId";
    private static final String SQL_REMOVE_FEED_USER_BY_ID = "DELETE FROM feed WHERE USER_ID = :userId";
    private static final String SQL_REMOVE_FRIENDS_USER_BY_ID = "DELETE FROM friends WHERE USER_ID = :userId";
    private static final String SQL_REMOVE_FEEDBACKS_USER_BY_ID = "DELETE FROM FEEDBACKS WHERE USER_ID = :userId";
    private static final String SQL_REMOVE_USERS_USER_BY_ID = "DELETE FROM USERS WHERE ID = :userId";
    @Autowired
    private NamedParameterJdbcTemplate jdbc;


    /**
     * Удаление всех пользователей
     */
    @Override
    public void removeAllUsers() {
        jdbc.update("DELETE FROM likes", new MapSqlParameterSource());
        jdbc.update("DELETE FROM reviews", new MapSqlParameterSource());
        jdbc.update("DELETE FROM feed", new MapSqlParameterSource());
        jdbc.update("DELETE FROM friends", new MapSqlParameterSource());
        jdbc.update("DELETE FROM FEEDBACKS", new MapSqlParameterSource());
        jdbc.update("DELETE FROM users", new MapSqlParameterSource());
    }


    /**
     * Удаление пользователя и смеждных данных по ID
     */
    @Override
    public void removeUsersById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", id);
        jdbc.update(SQL_REMOVE_LIKE_USER_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_REVIEWS_USER_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_FEED_USER_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_FRIENDS_USER_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_FEEDBACKS_USER_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_USERS_USER_BY_ID, mapSqlParameterSource);
    }


    /**
     * Удаление фильма и смеждных данных по ID
     */
    @Override
    public void removeFilmsById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("filmId", id);
        jdbc.update(SQL_REMOVE_LIKE_FILM_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_GENRES_FILM_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_DIRECTORS_FILM_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_REVIEWS_FILM_BY_ID, mapSqlParameterSource);
        jdbc.update(SQL_REMOVE_FILM_BY_ID, mapSqlParameterSource);
    }


    /**
     * Удаление всех фильмов
     */
    @Override
    public void removeAllFilms() {
        jdbc.update("DELETE FROM likes", new MapSqlParameterSource());
        jdbc.update("DELETE FROM films_genres", new MapSqlParameterSource());
        jdbc.update("DELETE FROM films_directors", new MapSqlParameterSource());
        jdbc.update("DELETE FROM reviews", new MapSqlParameterSource());
        jdbc.update("DELETE FROM films", new MapSqlParameterSource());
    }


}
