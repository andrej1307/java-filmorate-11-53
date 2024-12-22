package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование ограничений на значения полей класса User.
 * Автономный тест (Junit).
 */
// @SpringBootTest
// @AutoConfigureMockMvc
class UserTest {
    private Validator validator;

    /**
     * Перед каждым тестом готовим Validator
     */
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Тестирование email пользователя
     */
    @Test
    void testInvalidEmail() throws Exception {
        User user = new User("",
                "userTest",
                "Testing user",
                LocalDate.now().minusYears(22));

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Тестирование login пользователя
     */
    @Test
    void testInvalidLogin() throws Exception {
        User user = new User("user1234@test",
                "",  // login не должен быть пустым
                "Testing user",
                LocalDate.now().minusYears(32));

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());

        // login должен содержать только буквы и цифры
        user.setLogin("yu%3242 @#");
        violations.clear();
        violations = validator.validate(user, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Тестируем корректность даты рождения
     */
    @Test
    void testInvalidBirthday() throws Exception {
        User user = new User("user1234@test",
                "user1234",
                "Testing user",
                LocalDate.now().plusDays(1)); // Дата рождения в будущем

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Тестируем отсутствие ошибок при корректном заполнение полей.
     */
    @Test
    void testUserOk() {
        User user = new User("user1234@test",
                "user1234",
                "Testing user",
                LocalDate.now().minusYears(18));

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnBasic.class);
        assertTrue(violations.isEmpty(), violations.toString());
    }
}
