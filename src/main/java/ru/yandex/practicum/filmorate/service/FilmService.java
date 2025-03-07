package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmService {

    Collection<Film> findAllFilms();

    Film getFilmById(Integer id);

    Film addNewFilm(Film film);

    Film updateFilm(Film updFilm);

    String onDelete();

    Integer addNewLike(Integer filmId, Integer userId);

    Integer removeLike(Integer filmId, Integer userId);

    Collection<Film> findPopularFilms(int count);

    Map<String, String> getFilmRank(Integer filmId);
}
