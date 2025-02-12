package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreStorage genereStorage;

    public GenreService(GenreStorage genereStorage) {
        this.genereStorage = genereStorage;
    }

    public Collection<Genre> getAllGenres() {
        return genereStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genereStorage.getGenre(id).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + id));
    }
}
