package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс реализации запросов к информации о фильмах
 */
@Slf4j
@Service
public class FilmService {

    protected FilmStorage films = Storages.getFilmStorage();

    /**
     * Метод поиска всех фильмов
     *
     * @return - список фильмов
     */
    public Collection<Film> findAllFilms() {
        log.debug("Service: Ищем все фильмы {}.", films.findAllFilms().size());
        return films.findAllFilms();
    }

    /**
     * Метод поиска фильма по идентификатору
     *
     * @param id - идентификатор
     * @return - найденный фильм
     */
    public Film getFilmById(Integer id) {
        log.debug("Service: Ищем фильм id={}.", id);
        return films.getFilmById(id);
    }

    /**
     * Метод добавления нового фильма.
     *
     * @param film - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    public Film addNewFilm(Film film) {
        film.setRank(0); // Для предотвращения ручного ввода рейтинга
        log.debug("Service: Добавляем информацию о фильме: {}.", film.toString());
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
        Film film = new Film(films.getFilmById(id));

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

        log.debug("Service: Updating film id={} : {}", id, film.toString());
        return films.updateFilm(film);
    }

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    public String onDelete() {
        log.debug("Service: Удаляем все фильмы.");
        films.removeAllFilms();
        return "Все фильмы удалены.";
    }

    public Integer addNewLike(Integer filmId, Integer userId) {
        log.debug("Service: Добавляем \"лайк\" фильму {}, от пользователя {}.", filmId, userId);
        if (UserService.users.getUserById(userId) == null) {
            throw new NotFoundException("Не найден пользователь id=" + userId);
        }

        films.getFilmById(filmId).setRank(films.addNewLike(filmId, userId));
        return films.getFilmById(filmId).getRank();
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        log.debug("Service: Удаляем \"лайк\" у фильма {}, от пользователя {}.", filmId, userId);
        films.getFilmById(filmId).setRank(films.removeLike(filmId, userId));
        return films.getFilmById(filmId).getRank();
    }

    public Collection<Film> findPopularFilms(int count) {
        List<Film> popularFilms = new ArrayList<>();
        popularFilms = films.findAllFilms().stream()
                .sorted(Comparator.comparing(Film::getRank).reversed())
                .collect(Collectors.toList());
        if (count > popularFilms.size()) {
            count = popularFilms.size();
        }
        return popularFilms.subList(0, count);
    }

    public Map<String, String> getFilmRank(Integer filmId) {
        Map<String, String> response = new HashMap<>();
        response.put("Фильм  :", films.getFilmById(filmId).toString());
        response.put("Рейтинг:", films.getFilmById(filmId).getRank().toString());
        return response;
    }
}
