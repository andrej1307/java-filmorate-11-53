package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorServiceImpl implements DirectorService {

    @Autowired
    private DirectorStorage directorDbStorage;

    @Override
    public List<Director> getAllDirectors() {
        return directorDbStorage.findAll();
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        return Optional.ofNullable(directorDbStorage.findById(id));
    }

    @Override
    public Director createDirector(Director director) {
        directorDbStorage.save(director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        directorDbStorage.update(director);
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        directorDbStorage.delete(id);
    }

}
