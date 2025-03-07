package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorServiceImpl;

import java.util.List;

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
    public Director getDirectorById(@PathVariable int id) {
        return directorServiceImpl.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody Director director) {
        return directorServiceImpl.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorServiceImpl.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorServiceImpl.deleteDirector(id);
    }
}
