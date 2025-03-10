package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorServiceImpl;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {

    @Autowired
    private DirectorServiceImpl directorServiceImpl;

    @GetMapping
    public List<Director> getAllDirectors() {
        return directorServiceImpl.getAllDirectors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable int id) {
        Optional<Director> director = directorServiceImpl.getDirectorById(id);
        return director.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Director> createDirector(@RequestBody Director director) {
        Director savedDirector = directorServiceImpl.createDirector(director);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDirector);
    }


    @PutMapping
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        if (director == null) {
            log.warn("Данные директора не предоставлены");
            return ResponseEntity.badRequest().build();
        }

        Integer id = director.getId();
        if (id == null) {
            log.warn("ID директора не предоставлен");
            return ResponseEntity.badRequest().build();
        }
        try {
            Director updatedDirector = directorServiceImpl.updateDirector(director);
            log.info("Директор с ID: {} успешно обновлен", updatedDirector.getId());
            return ResponseEntity.ok(updatedDirector);
        } catch (NotFoundException e) {
            log.warn("Попытка обновить несуществующего директора с ID: {}", director.getId());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Некорректные данные для обновления директора: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorServiceImpl.deleteDirector(id);
    }
}
