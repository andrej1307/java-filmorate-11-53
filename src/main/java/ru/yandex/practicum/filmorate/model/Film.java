package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.LegalFilmDate;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Класс описания фильма.
 */
@Data
@EqualsAndHashCode(of = {"name", "releaseDate", "mpa", "genres"})
public class Film {

    @NotNull(groups = {Marker.OnUpdate.class}, message = "id должен быть определен")
    protected Integer id;

    @NotBlank(message = "Название фильма не может быть пустым.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private String name;

    @Size(min = 0, max = 200, message = "Максимальная длина описания - 200 символов.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private String description;

    @LegalFilmDate(groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть положительным числом",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private int duration;

    // рейтинг Ассоциации кинокомпаний
    @NotNull(groups = {Marker.OnBasic.class}, message = "рейтинг MPA должен быть определен")
    private Mpa mpa;

    // жанры фильма
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }

    public void clearGenres() {
        genres.clear();
    }
}
