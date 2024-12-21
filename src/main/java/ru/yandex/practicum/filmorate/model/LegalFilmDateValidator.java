package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Проверка аннотированного поля на допустимые значения даты
 */
public class LegalFilmDateValidator implements ConstraintValidator<LegalFilmDate, LocalDate> {
    private LocalDate minimumDate;
    private LocalDate maximumDate;

    @Override
    public void initialize(LegalFilmDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
        maximumDate = LocalDate.now();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null ||
                (value.isAfter(minimumDate) && !value.isAfter(maximumDate));
    }
}
