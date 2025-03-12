package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAll();

    Optional<Director> findDirectorById(int id);

    Director add(Director director);

    Director update(Director director);

    void delete(int id);

    void saveFilmDirectors(Film film);

    Collection<Director> findDirectorsByFilmId(Integer filmId);

    Collection<FilmDirector> findAllFilmDirector();

    Collection<Director> findDirectorsByName(String nameSubstring);

}
