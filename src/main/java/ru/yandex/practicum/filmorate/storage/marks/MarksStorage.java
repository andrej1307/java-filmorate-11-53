package ru.yandex.practicum.filmorate.storage.marks;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MarksStorage {

    // добавление "оценки" к фильму
    void addUserMark(Integer filmId, Integer userId, Integer mark);

    // удаление "оценки" к фильму
    void removeUserMark(Integer filmId, Integer userId);

    void calculateFilmMarks();

}
