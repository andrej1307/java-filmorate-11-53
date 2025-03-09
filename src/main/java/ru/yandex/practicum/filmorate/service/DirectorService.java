package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorService {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
