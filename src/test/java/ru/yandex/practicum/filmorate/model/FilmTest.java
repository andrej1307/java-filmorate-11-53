
package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование ограничений на значения полей класса Film
 */
class FilmTest {
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
     * Проверка непустого названия фильма.
     */
    @Test
    void testName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Testing film");
        film.setReleaseDate(LocalDate.now().minusYears(10));
        film.setDuration(60);
        film.setMpa(new Mpa(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка допусимого размера описания.
     */
    @Test
    void testDescription() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890");
        film.setReleaseDate(LocalDate.now().minusYears(10));
        film.setDuration(60);
        film.setMpa(new Mpa(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка допустимой даты выпуска фильма
     */
    @Test
    void testReleaseDate() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Testing film ReleaseDate");
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(60);
        film.setMpa(new Mpa(1));

        // Проверяем на контроль даты в будущем
        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());

        // Проверяем на контроль минимальной даты выхода фильма
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        violations.clear();
        violations = validator.validate(film, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка допустимой длительности фильма
     */
    @Test
    void testDuration() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Testing film ReleaseDate");
        film.setReleaseDate(LocalDate.now().minusYears(10));
        film.setDuration(0);
        film.setMpa(new Mpa(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnBasic.class);
        assertFalse(violations.isEmpty());
    }

    /**
     * Тестируем отсутствие ограничений при корректном создании фильма
     */
    @Test
    void testFilmOk() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Testing film ReleaseDate");
        film.setReleaseDate(LocalDate.now().minusYears(10));
        film.setDuration(60);
        film.setMpa(new Mpa(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnBasic.class);
        assertTrue(violations.isEmpty(), violations.toString());
    }

}
