package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;

@Repository
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {


        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, new DirectorRowMapper());
    }

    @Override
    public Director findById(int id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new DirectorRowMapper());
    }

    @Override
    public void save(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        jdbcTemplate.update(sql, director.getName());
    }

    @Override
    public void update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Collection<FilmDirector> findAllFilmDirector() {
        return List.of();
    }

    @Override
    public void saveFilmDirectors(Film film) {

    }

    @Override
    public Collection<Director> findDirectorsByFilmId(Integer filmId) {
        return List.of(new Director(1, "Стивен Спилберг"),
                new Director(2, "Люк Бессон"));
    }

    @Override
    public Collection<Integer> findDirectorsByName(String nameSubstring) {
        return List.of();
    }
}
