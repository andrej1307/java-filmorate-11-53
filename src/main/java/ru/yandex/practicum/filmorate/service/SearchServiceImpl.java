package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FilmStorage films;

    /**
     * Возвращает список фильмов, отфильтрованных по названию и/или имени режиссера.
     *
     * @param stringSearch   строка поиска
     * @param titleSearch    флаг, указывающий, нужно ли искать по названию фильма
     * @param directorSearch флаг, указывающий, нужно ли искать по имени режиссера
     * @return список фильмов, отфильтрованных по названию и/или имени режиссера
     */
    @Override
    public Collection<Film> searchFilms(String stringSearch, Boolean titleSearch, Boolean directorSearch) {

        // получаем отсортированный список фильмов по рейтингу
        Collection<Film> listFilms = films.findPopularFilms();

        // фильтруем фильмы по названию и имени режиссера
        listFilms = listFilms.stream()
                .filter(film -> {
                    boolean nameMatch = titleSearch && film.getName().toLowerCase().contains(stringSearch.toLowerCase());
                    boolean directorMatch = directorSearch && film.getDirectors()
                            .stream().anyMatch(director ->
                                    director != null && director.getName() != null && director.getName()
                                            .toLowerCase().contains(stringSearch.toLowerCase()));
                    return nameMatch || directorMatch;
                })
                .toList();
        return listFilms;
    }
}
