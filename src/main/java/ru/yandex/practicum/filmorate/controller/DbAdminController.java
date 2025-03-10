package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.DbAdminService;

/**
 * Класс обработки http запросов к информации о фильмах.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class DbAdminController {

    private final DbAdminService service;

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping("/films/all")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFilm() {
        log.info("Удаляем все фильмы.");
        return service.removeAllFilms();
    }

    /**
     * Удаление фильма по ID
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping("/films/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFilmById(@PathVariable @Min(0) Integer filmId) {
        log.info("Удаляем фильм по ID.");
        return service.removeFilmsById(filmId);
    }

    /**
     * Удаление всех пользователей
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping("/users/all")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser() {
        log.info("Удаляем всех пользователей.");
        return service.removeAllUsers();
    }

    /**
     * Удаление пользователей по ID
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUserById(@PathVariable @Min(0) Integer userId) {
        log.info("Удаляем юзера по ID.");
        return service.removeUsersById(userId);
    }


}
