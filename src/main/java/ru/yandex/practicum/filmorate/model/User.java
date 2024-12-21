package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
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

    @PastOrPresent(message = "Дата рождения не может быть в будущем.", groups = {Marker.OnBasic.class, Marker.OnUpdate.class})
    private LocalDate birthday;

    /**
     * Конструктор копирования сведений о пользователе
     *
     * @param original - объект копирования
     */
    public User(User original) {
        this.id = original.getId();
        this.email = original.getEmail();
        this.login = original.getLogin();
        this.name = original.getName();
        this.birthday = original.getBirthday();
    }
}
