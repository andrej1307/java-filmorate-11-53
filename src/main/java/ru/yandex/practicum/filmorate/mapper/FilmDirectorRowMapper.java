package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Чтене объектов связи фильма и режиссера
 * применяется к объединенным таблицам directors и films_directors
 */
@Component
public class FilmDirectorRowMapper implements RowMapper<FilmDirector> {
    @Override
    public FilmDirector mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FilmDirector filmDirector = new FilmDirector();
        filmDirector.setFilmId(resultSet.getInt("film_id"));
        Director director = new Director();
        director.setId(resultSet.getInt("director_id"));
        director.setName(resultSet.getString("director_name"));
        filmDirector.setDirector(director);
        return filmDirector;
    }
}
