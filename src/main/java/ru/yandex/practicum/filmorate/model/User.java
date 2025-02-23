package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;

/**
 * Класс описания пользователя.
 */
@Data
@EqualsAndHashCode(of = {"email"})
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class User {

    @NotNull(groups = {Marker.OnUpdate.class}, message = "id должен быть определен")
    protected Integer id;

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

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
