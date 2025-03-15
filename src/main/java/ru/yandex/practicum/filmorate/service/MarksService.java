package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface MarksService {

    void addUserMark(Integer filmId, Integer userId, Integer mark);

    void removeUserMArk(Integer filmId, Integer userId);

    Collection<Film> findPopularFilms(int count);

    String calculateFilmMarks();

}
