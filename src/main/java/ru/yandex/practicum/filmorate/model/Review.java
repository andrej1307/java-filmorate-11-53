package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.Name;
import ru.yandex.practicum.filmorate.validator.Marker;

/**
 * Класс отзыва к фильму
 */
@Data
@EqualsAndHashCode(of = {"content", "isPositive", "filmId", "userId"})
public class Review {

    @NotNull(groups = {Marker.OnUpdate.class}, message = "id должен быть определен")
    protected Integer reviewId;

    @Size(min = 0, max = 200, message = "Максимальная длина отзыва - 200 символов.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    @NotBlank(message = "Отзыв не может быть пустым.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private Boolean isPositive;

    @NotNull(message = "iD фильма должен быть указан.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private Integer filmId;

    @NotNull(message = "iD пользователя должен быть указан.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private Integer userId;

    private Integer useful;

    public boolean getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }
}
