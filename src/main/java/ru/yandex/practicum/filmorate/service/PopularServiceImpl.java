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

    /**
     * Возвращает список самых популярных фильмов.
     *
     * @param year    год, по которому нужно отфильтровать фильмы
     * @param genreId идентификатор жанра, по которому нужно отфильтровать фильмы
     * @param count   максимальное количество фильмов, которые нужно вернуть
     * @return список самых популярных фильмов, отфильтрованных по году и/или жанру
     * и ограниченных указанным количеством фильмов
     */
    @Override
    public Collection<Film> getPopular(Integer year, Integer genreId, Integer count) {

        Collection<Film> listFilms = films.findPopularFilms();

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
