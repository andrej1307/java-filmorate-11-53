package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
public interface MpaService {

    Collection<Mpa> filndAllMpa();

    Mpa findMpa(Integer id);
}
