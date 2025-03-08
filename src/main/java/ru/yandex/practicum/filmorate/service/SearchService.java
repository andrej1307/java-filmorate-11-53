package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface SearchService {

    List<Film> searchFilms(String stringSearch, Boolean titleSearch, Boolean directorSearch);

}
