package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.*;

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
    public Director save(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue()); // Установка сгенерированного ID
        return director;
    }

    @Override
    public Director update(Director director) {
        Integer id = director.getId();
        if (id == null) {
            throw new IllegalArgumentException("ID директора не может быть null");
        }

        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Директор с ID " + director.getId() + " не найден");
        }
        return director;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void saveFilmDirectors(Film film) {
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    @Override
    public Collection<Director> findDirectorByFilmId(Integer filmId) {
        String sql = "SELECT d.id, d.name " +
                "FROM directors d " +
                "JOIN films_directors fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?";

        return jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        });
    }

    @Override
    public Collection<FilmDirector> findAllFilmDirector() {
        String sql = "SELECT f.id AS film_id, d.id AS director_id, d.name AS director_name " +
                "FROM films f " +
                "LEFT JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id";

        List<FilmDirector> filmDirectors = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            Integer filmId = rs.getInt("film_id");
            Integer directorId = rs.getInt("director_id");
            String directorName = rs.getString("director_name");

            FilmDirector filmDirector = new FilmDirector();
            filmDirector.setFilmId(filmId);

            if (directorId != 0) {
                filmDirector.setDirectorId(directorId);
                filmDirector.setDirectorName(directorName);
            } else {
                filmDirector.setDirectorId(null);
                filmDirector.setDirectorName("Без режиссёра");
            }

            filmDirectors.add(filmDirector);
        });

        return filmDirectors;
    }

    @Override
    public Collection<Director> findDirectorsByName(String nameSubstring) {
        String sql = "SELECT d.id, d.name " +
                "FROM directors d " +
                "WHERE d.name LIKE ?";

        return jdbcTemplate.query(sql, new Object[]{"%" + nameSubstring + "%"}, (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        });
    }
}
