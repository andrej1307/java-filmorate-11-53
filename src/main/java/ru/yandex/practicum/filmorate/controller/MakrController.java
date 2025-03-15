package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MarksService;
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
@RequestMapping
@AllArgsConstructor
public class MakrController {

    private final MarksService marksService;

    @PutMapping("/films/{id}/user/{userId}/marks/{mark}")
    @ResponseStatus(HttpStatus.OK)
    public String addMArk(@PathVariable("id") Integer filmId,
                                       @PathVariable("userId") Integer userId,
                                       @PathVariable("mark") Integer mark) {
        log.debug("Добавляем \"оценку \" фильму {}, от пользователя {}.", filmId, userId);
        marksService.addUserMark(filmId, userId, mark);
        return "Оценка добавлена, рейтинг будет обновлен";
    }

    @DeleteMapping("/films/{id}/user/{userId}/marks")
    @ResponseStatus(HttpStatus.OK)
    public String removeMark(@PathVariable("id") Integer filmId,
                                          @PathVariable("userId") Integer userId
                                          ) {
        log.debug("Удаляем \"оценку\" у фильма {}, от пользователя {}.", filmId, userId);
        marksService.removeUserMArk(filmId, userId);
        return "Оценка добавлена, рейтинг будет обновлен";
    }



}
