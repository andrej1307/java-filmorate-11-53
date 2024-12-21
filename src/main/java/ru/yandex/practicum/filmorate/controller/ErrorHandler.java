package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorMessage;
import ru.yandex.practicum.filmorate.model.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.model.Violation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс обработки исключений при обработке поступивших http запросов
 */
@ControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    /**
     * Обработка исключения ConstraintViolationException - при проверке ограничений объекта
     *
     * @param e - исключение
     * @return - список нарушений для отображения в теле ответа
     */
    @ResponseBody
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

        log.info("404 {}.", e.getMessage());
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
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.info("404 {}.", e.getMessage());
        return new ValidationErrorResponse(violations);
    }

    /**
     * Метод обработки пользовательского исключения ValidationException
     *
     * @param exception - исключкние проверки данных
     * @return - объект для http ответа с сообщением об ошибке
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> onValidationException(ValidationException exception) {
        log.info("{} {}.", exception.getHttpStatus(), exception.getMessage());
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(new ErrorMessage(exception.getMessage()));
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
