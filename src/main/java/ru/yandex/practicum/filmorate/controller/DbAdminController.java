package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * Удаление фильма по ID
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping("/films/{filmId}")
    public String deleteFilmById(@PathVariable @Min(0) Integer filmId) {
        log.info("Удаляем фильм по ID.");
        return service.removeFilmsById(filmId);
    }

    /**
     * Удаление пользователей по ID
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping("/users/{userId}")
    public String deleteUserById(@PathVariable @Min(0) Integer userId) {
        log.info("Удаляем юзера по ID.");
        return service.removeUsersById(userId);
    }


}
