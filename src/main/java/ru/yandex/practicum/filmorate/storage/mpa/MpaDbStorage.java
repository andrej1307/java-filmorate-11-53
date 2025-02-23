package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private static final String SQL_GET_ALL_MPA = "SELECT * FROM mpa";
    private static final String SQL_GET_MPA = "SELECT * FROM mpa WHERE id = :id";

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<Mpa> findAllMpa() {
        try {
            return jdbc.query(SQL_GET_ALL_MPA, new MpaRowMapper());
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    @Override
    public Optional<Mpa> findMpa(Integer id) {
        try {
            Mpa mpa = jdbc.queryForObject(SQL_GET_MPA,
                    new MapSqlParameterSource()
                            .addValue("id", id),
                    new MpaRowMapper());
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
