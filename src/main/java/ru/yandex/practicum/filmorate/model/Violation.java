package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Класс описания нарушений при проверке ограничений.
 */
@Getter
@RequiredArgsConstructor
public class Violation {
    private final String fieldName; // Наименование поля объекта
    private final String message;   // Описание нарушения
}
