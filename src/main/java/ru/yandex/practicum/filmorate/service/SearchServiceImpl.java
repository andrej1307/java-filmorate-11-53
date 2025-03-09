package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FilmStorage films;

    @Override
    public List<Film> searchFilms(String stringSearch, Boolean titleSearch, Boolean directorSearch) {

// получаем из базы список всех фильмов
        Collection<Film> listFilms = films.findAllFilms();

// фильтруем фильмы по названию и имени режиссера
        List<Film> filteredFilms = listFilms.stream()
                .filter(film -> {
                    boolean nameMatch = titleSearch && film.getName().contains(stringSearch);
                    boolean directorMatch = directorSearch && film.getDirectors()
                            .stream().anyMatch(director -> director.getName().contains(stringSearch));
                    return nameMatch || directorMatch;
                })
                .toList();

// сортируем фильмы по рейтингу и возвращаем из метода
        return filteredFilms.stream()
                .sorted(Comparator.comparing(film ->
                        films.getFilmRank(film.getId()), Comparator.reverseOrder()))
                .toList();
    }
}
