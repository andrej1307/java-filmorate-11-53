package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularServiceImpl implements PopularService {

    @Autowired
    private final FilmStorage films;
    @Autowired
    private final GenreStorage genres;

    @Override
    public Collection<Film> getPopular(Integer year, Integer genreId, Integer count) {

        List<Film> films = new ArrayList<>();

        return List.of();
    }
}
