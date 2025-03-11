package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    // добавление нового фильма
    Film addNewFilm(Film newFilm);

    // чтение фильма по идентификатору
    Optional<Film> getFilmById(Integer filmId);

    // чтение фильма по списку идентификаторов
    Collection<Film> findFilmsByIds(List<Integer> filmsIds);

    // поиск всех фильмов
    Collection<Film> findAllFilms();

    // поиск самых популярных фильмов
    Collection<Film> findPopularFilms();

    // изменение сведений о фильме
    void updateFilm(Film updFilm);

    // добавление "лайка" к фильму
    Integer addNewLike(Integer filmId, Integer userId);

    // удаление "лайка" к фильму
    Integer removeLike(Integer filmId, Integer userId);

    // Чтение числа "лайков" у фильма
    Integer getFilmRank(Integer filmId);

    // Поиск общих фильмов у пользователей
    Collection<Film> findCommonFilms(Integer userId1, Integer userId2);

    void removeAllFilms();
}
