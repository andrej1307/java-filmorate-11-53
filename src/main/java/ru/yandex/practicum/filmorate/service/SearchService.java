package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface SearchService {

    Collection<Film> searchFilms(String stringSearch, Boolean titleSearch, Boolean directorSearch);

}
