package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;

@Slf4j
@RestController

public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @GetMapping("/directors")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> getAllDirectors() {
        log.info("Ищем все фильмы");
        return directorService.findAllDirectors();
    }

    @GetMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director findDirectorById(@PathVariable int id) {
        log.info("Ищем режиссера id = {}.", id);
        return directorService.findDirectorById(id);
    }

    @PostMapping("/directors")
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(@Validated(Marker.OnBasic.class) @RequestBody Director director) {
        log.info("Добавляем нового директора : {}.", director.toString());
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@Validated(Marker.OnUpdate.class) @RequestBody Director director) {
        log.info("Обновляем информацию о директоре : {}", director.toString());
        return directorService.updateDirector(director);

    }

    @DeleteMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable int id) {
        log.info("Удаляем директора id={}", id);
        directorService.deleteDirector(id);
    }

    @GetMapping("/films/director/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilmsByDirectorId(@PathVariable int id,
                                                 @RequestParam(required = false) String sortBy) {
        log.info("Получаем список фильмов по по директору [ {} ] с сортировкой по {}", id, sortBy);
        if (sortBy.equals("likes") || sortBy.equals("year")) {
            Collection<Film> films = directorService.getFilmsByDirectorId(id, sortBy);
            return films;
        } else {
            throw new ValidationException("Неправильные указание параметров поиска 'sortBy' ");
        }
    }

}
