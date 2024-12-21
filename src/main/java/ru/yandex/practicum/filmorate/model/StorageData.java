package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Класс данных для наследования классов модел
 */
@Data
public class StorageData {
    @NotNull(groups = {Marker.OnUpdate.class}, message = "id должен быть определен")
    Integer id;
}
