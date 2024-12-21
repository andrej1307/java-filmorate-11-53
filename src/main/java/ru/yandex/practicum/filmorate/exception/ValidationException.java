package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

/**
 * класс исключений прии проверки допустимых значений переменнх
 */
public class ValidationException extends RuntimeException {
    HttpStatus httpStatus;

    public ValidationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
