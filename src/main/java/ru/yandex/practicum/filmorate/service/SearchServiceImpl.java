package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FilmStorage films;

    @Override
    public List<Film> searchFilms(String stringSearch, Boolean titleSearch, Boolean directorSearch) {

        // получаем из базы список всех фильмов
        Collection<Film> draftFilms = films.findAllFilms();

        // создаём новый список фильмов, но уже с режиссёрами
        Collection<Film> listFilms = new ArrayList<>();
        draftFilms.forEach(film -> {
            listFilms.add(films.getFilmById(film.getId()).get());
        });

        // возвращаем найденные фильмы по подстроке и сортируем список по рейтингу
        return listFilms.stream()
                .filter(film -> {
                    boolean nameMatch = titleSearch && film.getName().contains(stringSearch);
                    boolean directorMatch = directorSearch && film.getDirectors().
                            stream().anyMatch(director -> director.getName().contains(stringSearch));
                    return nameMatch || directorMatch;
                })
                .sorted((film1, film2) ->
                        films.getFilmRank(film2.getId()).compareTo(films.getFilmRank(film1.getId())))
                .toList();
    }
}
