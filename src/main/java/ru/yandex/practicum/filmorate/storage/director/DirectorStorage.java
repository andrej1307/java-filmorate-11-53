package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director findById(int id);

    Director save(Director director);

    Director update(Director director);

    void delete(int id);

    void saveFilmDirectors(Film film);

    Collection<Director> findDirectorByFilmId(Integer filmId);

    Collection<FilmDirector> findAllFilmDirector();

    Collection<Director> findDirectorsByName(String nameSubstring);
}
