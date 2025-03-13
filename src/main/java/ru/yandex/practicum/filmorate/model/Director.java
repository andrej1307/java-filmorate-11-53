package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.Marker;

/**
 * Класс описания режиссеров
 */
@Data
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @NotNull(groups = {Marker.OnUpdate.class}, message = "id должен быть определен")
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private String name;
}
