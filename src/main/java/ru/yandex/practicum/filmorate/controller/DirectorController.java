package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @GetMapping("/directors")
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable int id) {
        Optional<Director> director = directorService.getDirectorById(id);
        return director.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/directors")
    public ResponseEntity<Director> createDirector(@RequestBody Director director) {
        Director savedDirector = directorService.createDirector(director);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDirector);
    }


    @PutMapping("/directors")
    public ResponseEntity<Director> updateDirector(@Validated(Marker.OnUpdate.class) @RequestBody Director director) {
        log.info("Обновляем информацию о директоре id={} : {}", director.getId(), director.toString());
        Director updatedDirector = directorService.updateDirector(director);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDirector);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable int id) {
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
