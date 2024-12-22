package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тестироание утилитарного класса создания хранилищ
 */
class StoragesTest {

    /**
     * Тестируем создание хранилища фильмов
     */
    @Test
    void getFilmStorage() {
        FilmStorage filmStorage = Storages.getFilmStorage();
        assertNotNull(filmStorage,
                "Хранилище фильмов не инициализируется.");
    }

    /**
     * Тестируем создание хранилища пользователей
     */
    @Test
    void getUerStorage() {
        UserStorage userStorage = Storages.getUerStorage();
        assertNotNull(userStorage,
                "Хранилище пользователей не инициализируется.");
    }
}