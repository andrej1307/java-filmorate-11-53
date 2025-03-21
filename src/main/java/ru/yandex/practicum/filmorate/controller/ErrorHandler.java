package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorMessage;

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
    public List<ErrorMessage> onConstraintValidationException(ConstraintViolationException e) {
        final List<ErrorMessage> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new ErrorMessage(
                                "[" + violation.getPropertyPath().toString() + "] " +
                                        violation.getMessage()
                        )
                )
                .collect(Collectors.toList());

        log.info("400 {}.", e.getMessage());
        return violations;
    }

    /**
     * Обработка исключения MethodArgumentNotValidException - при проверке аргумента метода
     *
     * @param e - исключение
     * @return - список нарушений для отображения в теле ответа
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorMessage> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<ErrorMessage> violations = e.getBindingResult().getFieldErrors().stream()
                .map(
                        error -> new ErrorMessage("[" + error.getField() + "] "
                                + error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.info("400 {}.", e.getMessage());
        return violations;
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundData(DataAccessException exception) {
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
    public ErrorMessage onHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.info("400 {}.", e.getMessage());
        return new ErrorMessage("В запросе отсутствуют необходимые данные." + e.getMessage());
        //        ResponseEntity
        //        .status(HttpStatus.BAD_REQUEST)
        //        .body(new ErrorMessage("В запросе отсутствуют необходимые данные."));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage internalException(final InternalServerException e) {
        log.warn("Error", e);
        return new ErrorMessage(e.getMessage());
    }

    /**
     * Обработка непредвиденного исключения
     *
     * @param e - исключение
     * @return - сообщение об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(final Exception e) {
        log.warn("Error", e);
        return new ErrorMessage(e.getMessage());
    }
}
