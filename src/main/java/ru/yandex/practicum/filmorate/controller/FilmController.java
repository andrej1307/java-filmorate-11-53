package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.PopularService;
import ru.yandex.practicum.filmorate.service.SearchService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;
import java.util.Map;

/**
 * Класс обработки http запросов к информации о фильмах.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;
    private final PopularService popularService;
    private final SearchService searchService;

    @Autowired
    public FilmController(FilmService service, PopularService popularService, SearchService searchService) {
        this.service = service;
        this.searchService = searchService;
        this.popularService = popularService;
    }

    /**
     * Метод поиска всех фильмов
     *
     * @return - список фильмов
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAllFilms() {
        log.info("Ищем все фильмы {}.", service.findAllFilms().size());
        return service.findAllFilms();
    }

    /**
     * Метод поиска фильма по идентификатору
     *
     * @param id - идентификатор
     * @return - найденный фильм
     */
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Integer id) {
        log.info("Ищем фильм id={}.", id);
        return service.getFilmById(id);
    }

    /**
     * Получает список самых популярных фильмов за определенный год, жанр и лимит.
     *
     * @param year    год, за который нужно получить список самых популярных фильмов
     * @param genreId идентификатор жанра, по которому нужно получить список самых популярных фильмов
     * @param count   лимит количества фильмов, которые нужно получить
     * @return коллекция самых популярных фильмов
     */
    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getReviews(@RequestParam(required = false) Integer year,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) Integer count) {
        log.info("Получаем список самых популярных фильмов за {} года, жанра {} и лимитом{}", year, genreId, count);
        return popularService.getPopular(year, genreId, count);
    }

    @GetMapping("/common")
    public Collection<Film> findCommonFilms(@RequestParam @Min(1) int userId,
                                            @RequestParam @Min(1) int friendId) {
        log.info("Ищем общие фильмы ползователя id={} и пользователя id={}.", userId, friendId);
        return service.findCommonFilms(userId, friendId);
    }

    /**
     * Метод добавления нового фильма.
     *
     * @param film - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@Validated(Marker.OnBasic.class) @RequestBody Film film) {
        log.info("Добавляем новй фильм: {}.", film.toString());
        return service.addNewFilm(film);
    }

    /**
     * Метод обновления информации о фильме.
     * При вызове метода промзводится проверка аннотаций только для маркера OnUpdate.class.
     * Кроме id любой другой параметр может отсутствовать
     *
     * @param updFilm - объект с обновленной информацией о фильме
     * @return - подтверждение обновленного объекта
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Validated(Marker.OnUpdate.class) @RequestBody Film updFilm) {
        Integer id = updFilm.getId();
        log.info("Обновляем информацию о фильме id={} : {}", id, updFilm.toString());
        return service.updateFilm(updFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> addLike(@PathVariable("id") Integer filmId,
                                       @PathVariable("userId") Integer userId) {
        log.debug("Добавляем \"лайк\" фильму {}, от пользователя {}.", filmId, userId);
        service.addNewLike(filmId, userId);
        return service.getFilmRank(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> removeLike(@PathVariable("id") Integer filmId,
                                          @PathVariable("userId") Integer userId) {
        log.debug("Удаляем \"лайк\" у фильма {}, от пользователя {}.", filmId, userId);
        service.removeLike(filmId, userId);
        return service.getFilmRank(filmId);
    }

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String onDelete() {
        log.info("Удаляем все фильмы.");
        return service.onDelete();
    }

    /**
     * Метод поиска фильмов по названию или режиссеру
     *
     * @param query строка поиска
     * @param by    параметр поиска (title - название фильма, director - режиссер)
     * @return - список найденных фильмов
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> search(@RequestParam String query,
                                   @RequestParam String by) {
        log.info("Получаем список фильмов по строке поиска [ {} ]", query);
        if (by.equals("title")) {
            return searchService.searchFilms(query, true, false);
        } else if (by.equals("director")) {
            return searchService.searchFilms(query, false, true);
        } else if (by.equals("title,director") || by.equals("director,title")) {
            return searchService.searchFilms(query, true, true);
        } else {
            throw new ValidationException("Неправильные указание параметров поиска 'by' ");
        }
    }
}
