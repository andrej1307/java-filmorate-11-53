package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genereStorage;

    public GenreServiceImpl(GenreStorage genereStorage) {
        this.genereStorage = genereStorage;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genereStorage.findAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genereStorage.findGenre(id).orElseThrow(() ->
                new NotFoundException("Не найден жанр id=" + id));
    }
}
