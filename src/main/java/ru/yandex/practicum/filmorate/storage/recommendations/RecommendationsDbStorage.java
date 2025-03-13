package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.Collection;
import java.util.List;

@Repository
@AllArgsConstructor
public class RecommendationsDbStorage implements RecommendationsStorage {
    private static final String SQL_FIND_ALL_FILMS = """
            SELECT films.*,
            mpa.NAME as mpa_name
            from
            (SELECT DISTINCT film_id
            FROM LIKES l
            WHERE USER_ID in
            (
            SELECT USER_ID FROM
            (
            SELECT count(FILM_ID) AS lyketop,
            USER_ID
            FROM LIKES ll
            WHERE USER_ID <>:userId and film_id IN (
            --внутренний запрос выбора фильмоф для подборки
            SELECT film_id
            FROM LIKES l
            WHERE USER_ID =:userId)
            GROUP BY USER_ID
            ORDER BY lyketop desc )
            --исключаем фильмы лайкнутые юзером для которого строим рекомендации
            ) AND FILM_ID NOT IN ( SELECT FILM_ID
            						FROM LIKES l2
            						WHERE USER_ID=:userId))
            left join FILMS ON FILM_ID =FILMs.ID
            LEFT JOIN MPA ON MPA_ID =mpa.ID
            """;
    private final NamedParameterJdbcTemplate jdbc;
    private final FilmDbStorage filmDbStorage;

    /**
     * Поиск всех фильмов
     *
     * @return - список фильмов
     */
    @Override
    public Collection<Film> getFilmsRecommendationsByUserId(Integer userId) {
        try {
            List<Film> films = jdbc.query(SQL_FIND_ALL_FILMS, new MapSqlParameterSource()
                            .addValue("userId", userId),
                    new FilmRowMapper());
            return filmDbStorage.updateFilmsEnviroment(films);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }


}
