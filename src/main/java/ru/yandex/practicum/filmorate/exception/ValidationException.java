package ru.yandex.practicum.filmorate.exception;

/**
 * класс исключений прии проверки допустимых значений переменнх
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
