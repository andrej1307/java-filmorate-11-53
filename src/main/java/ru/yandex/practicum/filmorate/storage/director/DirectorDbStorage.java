package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.FilmDirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {

    private final NamedParameterJdbcTemplate jdbc;

    public DirectorDbStorage(@Autowired NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SQL_FIND_ALL_DIRECTORS = "SELECT * FROM directors";

    @Override
    public Collection<Director> findAll() {
        try {
            Collection<Director> directors = jdbc.query(SQL_FIND_ALL_DIRECTORS, new DirectorRowMapper());
            return directors;
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    private static final String SQL_FIND_DIRECTOR_BY_ID =
            "SELECT * FROM directors WHERE id = :id";

    @Override
    public Optional<Director> findDirectorById(int id) {
        try {
            Director director = jdbc.queryForObject(SQL_FIND_DIRECTOR_BY_ID,
                    new MapSqlParameterSource()
                            .addValue("id", id),
                    new DirectorRowMapper());
            return Optional.ofNullable(director);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    private static final String SQL_INSERT_DIRECTOR =
            "INSERT INTO directors (name) VALUES (:name)";

    @Override
    public Director add(Director director) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        try {
            jdbc.update(SQL_INSERT_DIRECTOR,
                    new MapSqlParameterSource()
                            .addValue("name", director.getName()),
                    generatedKeyHolder
            );
        } catch (DataAccessException e) {
            throw new NotFoundException("Ошибка при записи сведений о режиссере : " +
                    e.getMessage());
        }

        // получаем идентификатор
        final Integer directorId = generatedKeyHolder.getKey().intValue();

        // возвращаем объект прочитанный из базы
        return findDirectorById(directorId).orElseThrow(() ->
                new InternalServerException("Ошибка при добавлении режиссера."));
    }


    private static final String SQL_UPDATE_DIRECTOR =
            "UPDATE directors SET name = :name WHERE id = :id";

    @Override
    public Director update(Director director) {
        // задаем параметры SQL запоса
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", director.getName());
        params.addValue("id", director.getId());

        // обновляем информацию
        int rowsUpdated = jdbc.update(SQL_UPDATE_DIRECTOR, params);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить информацию. Не наден режиссер " + director.toString());
        }

        // возвращаем объект прочитанный из базы
        return findDirectorById(director.getId())
                .orElseThrow(() -> new InternalServerException("Ошибка при обновлении режиссера."));
    }

    private static final String SQL_DELETE_FILMS_DIRECTOR =
            "DELETE FROM films_directors WHERE director_id = :id";
    private static final String SQL_DELETE_DIRECTOR =
            "DELETE FROM directors WHERE id = :id";

    @Override
    public void delete(int id) {
        // задаем параметры SQL запоса
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        jdbc.update(SQL_DELETE_FILMS_DIRECTOR, params);
        jdbc.update(SQL_DELETE_DIRECTOR, params);
    }

    private static final String SQL_DELETE_FILMS_DIRECTOR_BY_FILM =
            "DELETE FROM films_directors WHERE film_id = :film_id";
    private static final String SQL_UPDATE_FILMS_DIRECTORS =
            "MERGE INTO films_directors (film_id, director_id) "
                    + "VALUES (:film_id, :director_id)";

    @Override
    public void saveFilmDirectors(Film film) {
        Integer filmId = film.getId();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);

        // Удаляем устаревшую информацию
        jdbc.update(SQL_DELETE_FILMS_DIRECTOR_BY_FILM, params);

        // Сохраняем список режиссеров соответствующих фильму
        SqlParameterSource[] batch = film.getDirectors().stream()
                .map(director -> new MapSqlParameterSource()
                        .addValue("film_id", filmId)
                        .addValue("director_id", director.getId()))
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate(SQL_UPDATE_FILMS_DIRECTORS, batch);
    }

    private static final String SQL_FIND_DIRECTORS_BY_FILM_ID = """
            SELECT d.id, d.name
                FROM directors d
                INNER JOIN films_directors fd ON d.id = fd.director_id
                WHERE fd.film_id = :film_id
                """;

    @Override
    public Collection<Director> findDirectorsByFilmId(Integer filmId) {
        try {
            Collection<Director> directors = jdbc.query(SQL_FIND_DIRECTORS_BY_FILM_ID,
                    new MapSqlParameterSource()
                            .addValue("film_id", filmId),
                    new DirectorRowMapper());
            return directors;
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    private static final String SQL_FIND_ALL_FILM_DIRECTORS =
            "SELECT fd.film_id, fd.director_id, d.name AS director_name "
                    + "FROM films_directors fd LEFT JOIN directors d ON fd.director_id = d.id";

    @Override
    public Collection<FilmDirector> findAllFilmDirector() {
        try {
            Collection<FilmDirector> filmDirectors = jdbc.query(SQL_FIND_ALL_FILM_DIRECTORS,
                    new FilmDirectorRowMapper());
            return filmDirectors;
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    private static final String SQL_FIND_DIRECTORS_BY_NAME =
            "SELECT d.id, d.name FROM directors d WHERE d.name LIKE %:name%";

    @Override
    public Collection<Director> findDirectorsByName(String nameSubstring) {
        try {
            Collection<Director> directors = jdbc.query(SQL_FIND_ALL_FILM_DIRECTORS,
                    new MapSqlParameterSource()
                            .addValue("name", nameSubstring),
                    new DirectorRowMapper());
            return directors;
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }
}
