package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface PopularService {

    Collection<Film> getPopular(Integer year, Integer genreId, Integer count);
}
