package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorMessage;
import ru.yandex.practicum.filmorate.model.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.model.Violation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс обработки исключений при обработке поступивших http запросов
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Обработка исключения ConstraintViolationException - при проверке ограничений объекта
     *
     * @param e - исключение
     * @return - список нарушений для отображения в теле ответа
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());

        log.info("400 {}.", e.getMessage());
        return new ValidationErrorResponse(violations);
    }

    /**
     * Обработка исключения MethodArgumentNotValidException - при проверке аргумента метода
     *
     * @param e - исключение
     * @return - список нарушений для отображения в теле ответа
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.info("400 {}.", e.getMessage());
        return new ValidationErrorResponse(violations);
    }

    /**
     * Метод обработки пользовательского исключения ValidationException
     *
     * @param exception - исключкние проверки данных
     * @return - объект для http ответа с сообщением об ошибке
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage onValidationException(ValidationException exception) {
        log.info("400 {}.", exception.getMessage());
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundObject(NotFoundException exception) {
        log.info("404 {}.", exception.getMessage());
        return new ErrorMessage(exception.getMessage());
    }

    /**
     * Обработка исключения HttpMessageNotReadableException при поступлении пустого запроса
     *
     * @param e - исключкние генерируемое при отсутствии обязательных данных в теле запроса
     * @return - объект для http ответа с сообщением об ошибке
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> onHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.info("400 {}.", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("В запросе отсутствуют необходимые данные."));
    }

}
