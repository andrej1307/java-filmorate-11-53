package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationsService;

import java.util.Collection;

/**
 * Класс обработки http запросов о пользователях.
 */
@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class UserRecommendationsController {

    private final RecommendationsService recommendationsService;

    /**
     * Поиск фильмов для рекомендаций
     *
     * @param id - идентификатор пользователя
     * @return - список фильмов друзей
     */
    @GetMapping("/users/{id}/recommendations")
    public Collection<Film> findCommonFriends(@PathVariable("id") @Min(0) Integer id) {
        log.info("Список рекомендаций для пользователя: {}, {}.", id);
        return recommendationsService.getFilmsRecommendationsByUserId(id);
    }


}