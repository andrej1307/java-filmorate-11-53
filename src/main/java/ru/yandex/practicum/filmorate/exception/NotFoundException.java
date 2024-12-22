package ru.yandex.practicum.filmorate.exception;

/**
 * Класс исключения при отсутствии искомой информации
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
