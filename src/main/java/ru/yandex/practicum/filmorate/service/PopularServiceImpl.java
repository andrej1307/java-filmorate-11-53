package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PopularServiceImpl implements PopularService {

    private final FilmStorage films;

    @Override
    public Collection<Film> getPopular(Integer year, Integer genreId, Integer count) {

        // получаем отсортированный список фильмов по рейтингу
        Collection<Film> listFilms = films.findPopularFilms();

        // фильтруем фильмы по году и/или по жанру, если указаны
        // и возвращаем список фильмов, ограниченный указанным количеством фильмов
        listFilms = listFilms.stream()
                .filter(film -> {
                    boolean yearMatch = year == null || film.getReleaseDate().getYear() == year;
                    boolean genreMatch = genreId == null || film.getGenres()
                            .stream().anyMatch(genre -> genre.getId() == genreId);
                    return yearMatch && genreMatch;
                })
                .limit(Optional.ofNullable(count).orElse(Integer.MAX_VALUE))
                .toList();

        return listFilms;
    }
}
