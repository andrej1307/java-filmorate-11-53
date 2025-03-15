package ru.yandex.practicum.filmorate.storage.marks;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class MarksDbStorage implements MarksStorage {

    private static final String SQL_ADD_MARKS =
                "MERGE INTO marks (user_id, film_id, mark) VALUES (:userId, :filmId, :mark)";
    private static final String SQL_ADD_MARKS_CALCULATE ="MERGE INTO film_marks_calculate (film_id) VALUES (:filmId)";
    private static final String SQL_REMOVE_MARKS = "DELETE FROM marks WHERE user_id = :userId AND film_id = :filmId";

    private static final String SQL_CALCULATE_MARKS="""
 SELECT summark/countmark,
FILM_ID
FROM (
SELECT CAST (SUM(MARK) AS float) AS summark,
CAST( count(USER_ID) AS float) AS countmark,
FILM_ID 
FROM MARKS m 
WHERE FILM_ID IN (
SELECT film_id
FROM FILM_MARKS_CALCULATE)
GROUP BY FILM_ID) 
""";

    private final NamedParameterJdbcTemplate jdbc;


    /**
     * Добавление "оценки" к фильму.
     *
     * @param filmId - идентификатор фильма
     * @param userId - идентификатор пользователя
     * @return - число "лайков"
     */
    @Override
    public void addUserMark(Integer filmId, Integer userId, Integer mark) {
        int rowsUpdated = jdbc.update(SQL_ADD_MARKS, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("filmId", filmId)
                .addValue("mark", mark)
        );

        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }

        rowsUpdated = jdbc.update(SQL_ADD_MARKS_CALCULATE, new MapSqlParameterSource()
                .addValue("filmId", filmId)
        );
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    /**
     * Удаление "оценки" у фильма.
     *
     * @param filmId - идентификатор фильма
     * @param userId - идентификатор пользователя
     * @return - число "лайков"
     */
    @Override
    public void removeUserMark(Integer filmId, Integer userId) {
        jdbc.update(SQL_REMOVE_MARKS, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("filmId", filmId)
        );
        jdbc.update(SQL_ADD_MARKS_CALCULATE, new MapSqlParameterSource()
                .addValue("filmId", filmId));
    }

    /**
     * Подсчет "jwtyjr" фильма.
     *
     */
    @Override
    public void calculateFilmMarks() {
        jdbc.q(SQL_CALCULATE_MARKS, new MapSqlParameterSource());
    }

}
