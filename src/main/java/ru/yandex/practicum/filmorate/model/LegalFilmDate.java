package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Интерфейс аннотации проверки даты выпуска фильма
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LegalFilmDateValidator.class)
public @interface LegalFilmDate {
    String message() default "Дата выпуска фильма не должна быть ранее {value} и позднее текущей.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}
