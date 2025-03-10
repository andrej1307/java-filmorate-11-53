package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Сопоставление идентификатора фильма с соответствующим режисером
 */
@Data
public class FilmDirector {
    private Integer filmId;
    private Director director;
}

