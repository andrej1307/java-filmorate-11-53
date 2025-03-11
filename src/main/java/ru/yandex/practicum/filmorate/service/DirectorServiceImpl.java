package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final FilmStorage films;
    private final DirectorStorage directorDbStorage;

    @Override
    public List<Director> getAllDirectors() {
        return directorDbStorage.findAll();
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        return Optional.ofNullable(directorDbStorage.findById(id));
    }

    @Override
    public Director createDirector(Director director) {
        directorDbStorage.save(director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        directorDbStorage.update(director);
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        directorDbStorage.delete(id);
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(int directorId, String sortBy) {

        if (directorDbStorage.findById(directorId) == null) {
            throw new NotFoundException("Директор с id = " + directorId + " не найден");
        }

        // получаем отсортированный список фильмов по рейтингу
        Collection<Film> listFilms = films.findPopularFilms();

        listFilms = listFilms.stream()
                        .filter(film -> film.getDirectors()
                .stream().anyMatch(director -> director.getId() == directorId))
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
