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
    private static final String SQL_SELECT_DIRECTORS_BY_NAME = "SELECT d.id, d.name FROM directors d " +
            "WHERE d.name LIKE ?";
    private static final String SQL_SELECT_FILM_DIRECTORS = "SELECT f.id AS film_id, d.id AS " +
            "director_id, d.name AS director_name FROM films f " +
                "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id";
    private static final String SQL_SELECT_DIRECTORS_BY_FILM_ID = "SELECT d.id, d.name FROM directors d " +
                "JOIN film_directors fd ON d.id = fd.director_id WHERE fd.film_id = ?";
    private static final String SQL_INSERT_FILM_DIRECTORS = "INSERT INTO film_directors (film_id, director_id) " +
            "VALUES (?, ?)";
    private static final String SQL_DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ?";
    private static final String SQL_UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String SQL_INSERT_DIRECTOR = "INSERT INTO directors (name) VALUES (?)";
    private static final String SQL_SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = ?";

    /**
     * Метод для получения всех режиссёров из базы данных.
     *
     * @return список всех режиссёров
     */
    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, new DirectorRowMapper());
    }

    /**
     * Метод для получения режиссёра по его идентификатору.
     *
     * @param id идентификатор режиссёра
     * @return объект Director, соответствующий заданному идентификатору
     */
    @Override
    public Director findById(int id) {
        return jdbcTemplate.queryForObject(SQL_SELECT_DIRECTOR_BY_ID, new Object[]{id},
                new DirectorRowMapper());
    }

    /**
     * Метод для сохранения нового режиссёр-а/ов в базе данных.
     *
     * @param director объект Director, который нужно сохранить
     * @return сохраненный объект Director
     */
    @Override
    public Director save(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_DIRECTOR, new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue()); // Установка сгенерированного ID
        return director;
    }

    /**
     * Метод для обновления информации о режиссёре в базе данных.
     *
     * @param director объект Director, содержащий обновленные данные
     * @return обновленный объект Director
     */
    @Override
    public Director update(Director director) {
        Integer id = director.getId();
        if (id == null) {
            throw new IllegalArgumentException("ID директора не может быть null");
        }

        int rowsAffected = jdbcTemplate.update(SQL_UPDATE_DIRECTOR, director.getName(), director.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Директор с ID " + director.getId() + " не найден");
        }
        return director;
    }

    /**
     * Метод для удаления режиссёра из базы данных.
     *
     * @param id идентификатор режиссёра, которого нужно удалить
     */
    @Override
    public void delete(int id) {
        jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
    }

    /**
     * Метод для сохранения связей между фильмом и его режиссерами в базе данных.
     *
     * @param film объект Film, для которого нужно сохранить связи с режиссерами
     */
    @Override
    public void saveFilmDirectors(Film film) {

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(SQL_INSERT_FILM_DIRECTORS, film.getId(), director.getId());
        }
    }

    /**
     * Метод для получения всех режиссёров, связанных с фильмом по его идентификатору.
     *
     * @param filmId идентификатор фильма
     * @return список всех режиссёров, связанных с фильмом
     */
    @Override
    public Collection<Director> findDirectorsByFilmId(Integer filmId) {

        return jdbcTemplate.query(SQL_SELECT_DIRECTORS_BY_FILM_ID, new Object[]{filmId},
                (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        });
    }

    /**
     * Метод для получения всех связей между фильмами и их режиссёрами из базы данных.
     *
     * @return список всех связей между фильмами и их режиссёрами
     */
    @Override
    public Collection<FilmDirector> findAllFilmDirector() {

        List<FilmDirector> filmDirectors = new ArrayList<>();

        jdbcTemplate.query(SQL_SELECT_FILM_DIRECTORS, rs -> {
            Integer filmId = rs.getInt("film_id");
            Integer directorId = rs.getInt("director_id");
            String directorName = rs.getString("director_name");

            if (directorName != null) {
                FilmDirector filmDirector = new FilmDirector();
                filmDirector.setFilmId(filmId);
                filmDirector.setDirector(new Director());

                filmDirector.setDirector(new Director(directorId, directorName));
                filmDirectors.add(filmDirector);
            }

        });

        return filmDirectors;
    }

    /**
     * Метод для поиска режиссёров по части их имени.
     *
     * @param nameSubstring часть имени режиссёра для поиска
     * @return список всех режиссёров, у которых имя содержит заданную подстроку
     */
    @Override
    public Collection<Director> findDirectorsByName(String nameSubstring) {

        return jdbcTemplate.query(SQL_SELECT_DIRECTORS_BY_NAME, new Object[]{"%" + nameSubstring + "%"},
                (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        });
    }
}
