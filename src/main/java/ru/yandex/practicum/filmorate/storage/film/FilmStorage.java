package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    // добавление нового фильма
    Film addNewFilm(Film newFilm);

    // чтение фильма по идентификатору
    Optional<Film> getFilmById(Integer id);

    // поиск всех фильмов
    Collection<Film> findAllFilms();

    // поиск самых популярных фильмов
    Collection<Film> findPopularFilms(int count);

    // изменение сведений о фильме
    void updateFilm(Film updFilm);

    // добавление "лайка" к фильму
    Integer addNewLike(Integer filmId, Integer userId);

    // удаление "лайка" к фильму
    Integer removeLike(Integer filmId, Integer userId);

    void removeAllFilms();
}
