package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FilmStorage films;

    @Override
    public Collection<Film> searchFilms(String stringSearch, Boolean titleSearch, Boolean directorSearch) {

        // получаем отсортированный список фильмов по рейтингу
        Collection<Film> listFilms = films.findPopularFilms();

        // фильтруем фильмы по названию и имени режиссера
        listFilms = listFilms.stream()
                .filter(film -> {
                    boolean nameMatch = titleSearch && film.getName().contains(stringSearch);
                    boolean directorMatch = directorSearch && film.getDirectors()
                            .stream().anyMatch(director ->
                                    director != null && director.getName() != null && director.getName()
                                            .contains(stringSearch));
                    return nameMatch || directorMatch;
                })
                .toList();

        return listFilms;
    }
}
