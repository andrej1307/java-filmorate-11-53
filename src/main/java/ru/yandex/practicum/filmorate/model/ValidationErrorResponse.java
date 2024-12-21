package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс для формирования ответа об обнаруженных нарушениях при проверке ограничений на данные
 */
@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    // список обнаруженных нарушений
    private final List<Violation> violations;
}
