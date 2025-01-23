package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс реализации запросов к информации о фильмах
 */
@Slf4j
@Service
public class FilmService {

    private final FilmStorage films;
    private final UserStorage users;

    public FilmService(FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage users) {
        this.films = filmStorage;
        this.users = users;
    }

    /**
     * Метод поиска всех фильмов
     *
     * @return - список фильмов
     */
    public Collection<Film> findAllFilms() {
        return films.findAllFilms();
    }

    /**
     * Метод поиска фильма по идентификатору
     *
     * @param id - идентификатор
     * @return - найденный фильм
     */
    public Film getFilmById(Integer id) {
        return films.getFilmById(id).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + id));
    }

    /**
     * Метод добавления нового фильма.
     *
     * @param film - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    public Film addNewFilm(Film film) {
        if (films.findAllFilms().contains(film)) {
            throw new ValidationException("Фильм уже существует :"
                    + film.getName());
        }
        return films.addNewFilm(film);
    }

    /**
     * Метод обновления информации о фильме.
     *
     * @param updFilm - объект с обновленной информацией о фильме
     * @return - подтверждение обновленного объекта
     */
    public Film updateFilm(Film updFilm) {
        Integer id = updFilm.getId();
        Film film = films.getFilmById(id).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + id));

        // Обновляем информаию во временном объекте
        if (updFilm.getName() != null) {
            film.setName(updFilm.getName());
        }
        if (updFilm.getDescription() != null) {
            film.setDescription(updFilm.getDescription());
        }
        if (updFilm.getReleaseDate() != null) {
            film.setReleaseDate(updFilm.getReleaseDate());
        }
        if (updFilm.getDuration() > 0) {
            film.setDuration(updFilm.getDuration());
        }
        return film;
    }

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    public String onDelete() {
        films.removeAllFilms();
        return "Все фильмы удалены.";
    }

    public Integer addNewLike(Integer filmId, Integer userId) {
        Film film = films.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));
        users.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + userId));

        film.setRank(films.addNewLike(filmId, userId));
        return film.getRank();
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        Film film = films.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));
        users.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + userId));

        film.setRank(films.removeLike(filmId, userId));
        return film.getRank();
    }

    public Collection<Film> findPopularFilms(int count) {
        return films.findPopularFilms(count);
    }

    public Map<String, String> getFilmRank(Integer filmId) {
        Film film = films.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));

        Map<String, String> response = new HashMap<>();
        response.put("Фильм  ", film.getName());
        response.put("Рейтинг", film.getRank().toString());
        return response;
    }
}
