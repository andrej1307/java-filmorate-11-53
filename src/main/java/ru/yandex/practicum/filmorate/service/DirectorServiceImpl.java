package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    @Autowired
    private DirectorStorage directorStorage;

    @Autowired
    private FilmStorage filmStorage;

    @Override
    public Collection<Director> findAllDirectors() {
        return directorStorage.findAll();
    }

    @Override
    public Director findDirectorById(int id) {
        return directorStorage.findDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Не найден режиссер. id=" + id));
    }

    @Override
    public Director createDirector(Director director) {
        return directorStorage.add(director);
    }

    @Override
    public Director updateDirector(Director director) {
        directorStorage.update(director);
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        directorStorage.delete(id);
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(final int directorId, String sortBy) {

        final Director directorValid = directorStorage.findDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Не найден режиссер. id=" + directorId));

        // получаем отсортированный список фильмов по рейтингу
        Collection<Film> listFilms;

        listFilms = filmStorage.findPopularFilms().stream()
                .filter(film -> film.getDirectors().contains(directorValid))
                // .stream().anyMatch(director -> director.getId() == directorId))
                .toList();

        if ("year".equals(sortBy)) {
            listFilms = listFilms.stream()
                    .sorted((film1, film2) ->
                            film1.getReleaseDate().compareTo(film2.getReleaseDate()))
                    .toList();
        }
        return listFilms;
    }

}
