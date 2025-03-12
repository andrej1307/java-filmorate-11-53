package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface DirectorService {

    Collection<Director> findAllDirectors();

    Director findDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    // получение отсортироанного списка фильмов задаенного режиссера
    Collection<Film> getFilmsByDirectorId(int directorId, String sortBy);
}

