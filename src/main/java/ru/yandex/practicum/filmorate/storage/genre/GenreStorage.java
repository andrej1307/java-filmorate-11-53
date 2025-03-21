package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> findGenresByFilmId(Integer filmId);

    Collection<FilmGenre> findAllFilmWhithGenres();

    void saveFilmGeres(Film film);

    Collection<Genre> findAllGenres();

    Optional<Genre> findGenre(Integer id);
}
