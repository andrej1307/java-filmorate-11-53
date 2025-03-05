package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService{

    @Autowired
    private DirectorDbStorage directorDbStorage;

    @Override
    public List<Director> getAllDirectors() {
        return directorDbStorage.findAll();
    }

    @Override
    public Director getDirectorById(int id) {
        return directorDbStorage.findById(id);
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
