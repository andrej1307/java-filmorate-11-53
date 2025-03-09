package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PopularServiceImpl implements PopularService {

    private final FilmStorage films;

    @Override
    public List<Film> getPopular(Integer year, Integer genreId, Integer count) {

// получаем из базы список всех фильмов
        Collection<Film> listFilms = films.findAllFilms();

// фильтруем фильмы по году, если указан
        if (year != null) {
            listFilms = listFilms.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        }

// фильтруем фильмы по жанру, если указан
        if (genreId != null) {
            listFilms = listFilms.stream()
                    .filter(film -> film.getGenres()
                            .stream().anyMatch(genre -> genre.getId() == genreId))
                    .toList();
        }

// сортируем фильмы по рейтингу и возвращаем необходимое количество фильмов
        return listFilms.stream()
                .sorted(Comparator.comparing(film ->
                        films.getFilmRank(film.getId()), Comparator.reverseOrder()))
                .limit(Optional.ofNullable(count).orElse(Integer.MAX_VALUE))
                .toList();
    }
}
