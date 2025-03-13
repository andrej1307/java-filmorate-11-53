package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

import static java.lang.Math.min;

/**
 * Класс реализации запросов к информации о фильмах
 */
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage films;
    private final UserStorage users;
    private final FeedService feeds;

    public FilmServiceImpl(FilmStorage filmStorage, UserStorage users, FeedService feeds) {
        this.films = filmStorage;
        this.users = users;
        this.feeds = feeds;
    }

    /**
     * Метод поиска всех фильмов
     *
     * @return - список фильмов
     */
    @Override
    public Collection<Film> findAllFilms() {
        return films.findAllFilms();
    }

    /**
     * Метод поиска фильма по идентификатору
     *
     * @param id - идентификатор
     * @return - найденный фильм
     */
    @Override
    public Film getFilmById(Integer id) {
        return films.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Не найден фильм id=" + id));
    }

    /**
     * Метод добавления нового фильма.
     *
     * @param film - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    @Override
    public Film addNewFilm(Film film) {
        Optional<Film> existingFilm = films.findAllFilms().stream()
                .filter(film1 -> film1.equals(film))
                .findFirst();
        if (existingFilm.isPresent()) {
            throw new ValidationException("Фильм уже существует: " + existingFilm.get());
        }
        return films.addNewFilm(film);
    }

    /**
     * Метод обновления информации о фильме.
     *
     * @param updFilm - объект с обновленной информацией о фильме
     * @return - подтверждение обновленного объекта
     */
    @Override
    public Film updateFilm(Film updFilm) {
        Integer id = updFilm.getId();
        Film film = films.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Не найден фильм id=" + id));

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
        if (updFilm.getMpa() != null) {
            film.setMpa(updFilm.getMpa());
        }

        // в тестах Postman для спринта №13 метод update применяется для удаления жанров
        // поэтому при наличии у фильма жанов и режиссеров они всегдадолжны быть заданы
            film.setGenres(updFilm.getGenres());
            film.setDirectors(updFilm.getDirectors());

        films.updateFilm(film);

        return films.getFilmById(id).orElseThrow(() ->
                new InternalServerException("Ошибка обновления фильма id=" + id));
    }

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    @Override
    public String onDelete() {
        films.removeAllFilms();
        return "Все фильмы удалены.";
    }

    @Override
    public Integer addNewLike(Integer filmId, Integer userId) {
        films.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));
        users.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id =" + userId));

        Integer likeCount = films.addNewLike(filmId, userId);

        feeds.createFeed(userId, EventType.LIKE, Operation.ADD, filmId);

        return likeCount;
    }

    @Override
    public Integer removeLike(Integer filmId, Integer userId) {
        Film film = films.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));
        users.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + userId));

        Integer likeCount = films.removeLike(filmId, userId);

        feeds.createFeed(userId, EventType.LIKE, Operation.REMOVE, filmId);

        return likeCount;
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        List<Film> fP = new ArrayList<>(films.findPopularFilms());
        return fP.subList(0, min(fP.size(), count));
    }

    @Override
    public Map<String, String> getFilmRank(Integer filmId) {
        Film film = films.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Не найден фильм id=" + filmId));

        Map<String, String> response = new HashMap<>();
        response.put("Фильм ", film.getName());
        response.put("лайков", films.getFilmRank(filmId).toString());
        return response;
    }

    public Collection<Film> findCommonFilms(Integer userId, Integer friendId) {
        return films.findCommonFilms(userId, friendId);
    }
}
