package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PopularServiceImpl implements PopularService {

    private final FilmStorage films;

    @Override
    public List<Film> getPopular(Integer year, Integer genreId, Integer count) {

        // получаем из базы список всех фильмов
        Collection<Film> draftFilms = films.findAllFilms();

        // создаём новый список фильмов, но уже с жанрами
        Collection<Film> genreFilms = new ArrayList<>();
        draftFilms.forEach(film -> {
              genreFilms.add(films.getFilmById(film.getId()).get());
        });

        // делаем выборку по году и жанру
        List<Film> listFilms = genreFilms.stream()
                .filter(film ->
                        Optional.ofNullable(year).map(y ->
                                film.getReleaseDate().getYear() == y).orElse(true)
                     && Optional.ofNullable(genreId).map(g ->
                                film.getGenres().stream().anyMatch(genre ->
                                        genre.getId() == g)).orElse(true))
                .toList();

        // сортируем список по рейтингу и возвращаем необходимое количество фильмов
        return listFilms.stream()
                .sorted((film1, film2) ->
                        films.getFilmRank(film2.getId()).compareTo(films.getFilmRank(film1.getId())))
                        .limit(Optional.ofNullable(count).orElse(Integer.MAX_VALUE))
                .toList();
    }
}
