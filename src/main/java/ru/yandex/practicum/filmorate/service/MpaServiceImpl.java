package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Mpa> filndAllMpa() {
        return mpaStorage.findAllMpa();
    }

    @Override
    public Mpa findMpa(Integer id) {
        return mpaStorage.findMpa(id).orElseThrow(() ->
                new NotFoundException("Не найден рейтинг id=" + id));
    }
}
