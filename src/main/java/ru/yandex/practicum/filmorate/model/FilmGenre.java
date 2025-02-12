package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Сопоставление идентификатора фильма с соответствующим жанром
 */
@Data
public class FilmGenre {
    private Integer filmId;
    private Integer genreId;
    private String genreName;
}
