package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

/**
 * Класс описания пользователя.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(exclude = {"id", "name", "birthday"})
@AllArgsConstructor
@Validated
public class User extends StorageData {

    @NotBlank(message = "Email не может быть пустым", groups = Marker.OnBasic.class)
    @Email(message = "Email должен удовлетворять правилам формирования почтовых адресов.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private String email;

    @NotBlank(message = "login не может быть пустым", groups = Marker.OnBasic.class)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$", message = "login должен иметь длину от 6 до 12 символов, содержать буквы и цифры.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.",
            groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private LocalDate birthday;
}
