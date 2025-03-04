package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private static final String SQL_GET_ALL_GENRES = "SELECT * FROM genres";
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

    private static final String SQL_GET_GENRE = "SELECT * FROM genres WHERE id = :id";
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

    private static final String SQL_FIND_ALL_FILMS_WHITH_GENRES =
            "SELECT fg.*, g.name AS genre_name FROM films_genres AS fg INNER JOIN genres AS g ON fg.GENRE_ID = g.ID";
    /**
     * Поиск всех связей фильм - жанр
     * @return - список пар {filmId, genre}
     */
    @Override
    public Collection<FilmGenre> findAllFilmWhithGenres() {
        List<FilmGenre> filmsGenres;
        filmsGenres = jdbc.query(SQL_FIND_ALL_FILMS_WHITH_GENRES, new FilmGenreRowMapper());
        return filmsGenres;
    }


    private static final String SQL_FIND_GENRES_BY_FILM_ID = """
            SELECT fg.film_id, g.* 
            FROM films_genres AS fg INNER JOIN genres AS g ON fg.GENRE_ID = g.ID
            WHERE fg.film_id = :film_id 
            """;
    /**
     * Поиск жанров соответствующих фильму с указанным идентификатором
     *
     * @param filmId - идентификатор фильма
     * @return
     */
    @Override
    public Collection<Genre> findGenresByFilmId(Integer filmId) {
        try {
            Collection<Genre> genres = jdbc.query(SQL_FIND_GENRES_BY_FILM_ID,
                    new MapSqlParameterSource()
                            .addValue("film_id", filmId),
                    new GenreRowMapper());
            return genres;
        } catch (DataAccessException ignored) {
            return List.of();
        }
    }
}
