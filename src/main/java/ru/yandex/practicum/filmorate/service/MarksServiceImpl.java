package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.marks.MarksStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

import static java.lang.Math.min;

/**
 * Класс реализации запросов к информации о фильмах
 */
@Service
@AllArgsConstructor
public class MarksServiceImpl implements MarksService {

    private final MarksStorage marksStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;




    @Override
    public void addUserMark(Integer filmId, Integer userId, Integer mark) {
        filmStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id =" + userId));
        marksStorage.addUserMark(filmId,userId,mark);

    }

    @Override
    public void removeUserMArk(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + userId));
        marksStorage.removeUserMark(filmId,userId);

    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        return List.of();
    }

    @Override
    public String calculateFilmMarks() {
        return "";
    }

}
