package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Класс описания фильма.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(exclude = {"id", "description"}) // при сравнении не учитывать: id, description
@AllArgsConstructor
@Validated
public class Film extends StorageData {

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

    private Integer rank = 0;

    // рейтинг Ассоциации кинокомпаний
    private Integer MPA_id = 0;

    // жанры фильма
    private LinkedHashSet<String> genres = new LinkedHashSet<>();

}
