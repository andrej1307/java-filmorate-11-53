package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Проверка аннотированного поля на допустимые значения даты
 */
public class LegalFilmDateValidator implements ConstraintValidator<LegalFilmDate, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(LegalFilmDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || value.isAfter(minimumDate);
    }
}
