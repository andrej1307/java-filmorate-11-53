package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    private static final String SQL_GET_ALL_GENRES = "SELECT * FROM genres";
    private static final String SQL_GET_GENRE = "SELECT * FROM genres WHERE id = :id";

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    /**
     * Чтение всех жанров в справочнике
     *
     * @return
     */
    @Override
    public Collection<Genre> findAllGenres() {
        try {
            return jdbc.query(SQL_GET_ALL_GENRES, new GenreRowMapper());
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    /**
     * чтение жанра по идентификатору
     *
     * @param id - идентификатор жанра
     * @return - объект Optional
     */
    @Override
    public Optional<Genre> findGenre(Integer id) {
        try {
            Genre genre = jdbc.queryForObject(SQL_GET_GENRE,
                    new MapSqlParameterSource()
                            .addValue("id", id),
                    new GenreRowMapper());
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
