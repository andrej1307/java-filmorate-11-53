package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

/**
 * Утилитарный класс для определения реализаций хранилищ данных
 */
public final class Storages {
    private Storages() {
    }

    /**
     * Метод определения текущего хранилища информации о фильмах
     *
     * @return - актуальное хранилище
     */
    public static FilmStorage getFilmStorage() {
        return new InMemoryFilmStorage();
    }

    /**
     * Метод определения текущего хранилища информации о пользователях
     *
     * @return - актуальное хранилище
     */
    public static UserStorage getUerStorage() {
        return new InMemoryUserStorage();
    }
}
