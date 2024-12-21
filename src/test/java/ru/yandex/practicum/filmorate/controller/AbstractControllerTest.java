package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирования базового контроллера элементов
 */
class AbstractControllerTest {

    private AbstractController<User> userController = new AbstractController<>();
    private AbstractController<Film> filmController = new AbstractController<>();

    /**
     * Очистка данных перед тестами
     */
    @BeforeEach
    void setUp() {
        userController.clear();
        filmController.clear();
    }

    /**
     * чтение фильма.
     */
    @Test
    void getFilm() {
        createFilms(5);
        Film film = filmController.getElement(1);
        assertNotNull(film, "Фильм не читается.");
        assertEquals(1, film.getId(),
                "Фильм в начале списка не читается.");

        film = filmController.getElement(3);
        assertEquals(3, film.getId(),
                "Фильм в середине списка не читается.");

        film = filmController.getElement(5);
        assertEquals(5, film.getId(),
                "Фильм в конце списка не читается.");

        assertThrows(ValidationException.class,
                () -> {
                    filmController.getElement(1000);
                },
                "Попытка чтения несуществующего фильма должна приводить к исключению.");
    }

    /**
     * Чтение пользователя.
     */
    @Test
    void getUser() {
        createUsers(5);
        User user = userController.getElement(1);
        assertNotNull(user, "Пользователь не читается.");
        assertEquals(1, user.getId(),
                "Пользователь в начале списка не читается.");

        user = userController.getElement(3);
        assertEquals(3, user.getId(),
                "Пользователь в середине списка не читается.");

        user = userController.getElement(5);
        assertEquals(5, user.getId(),
                "Пользователь в конце списка не читается.");

        assertThrows(ValidationException.class,
                () -> {
                    userController.getElement(1000);
                },
                "Попытка чтения несуществующего пользователя должна приводить к исключению.");
    }

    /**
     * Поиск всех фильмов.
     */
    @Test
    void findAllFilms() {
        int filmsNumber = 4;
        createFilms(filmsNumber);
        Collection<Film> films = filmController.findAll();
        assertEquals(filmsNumber, films.size(),
                "Список фильмов не читается.");
    }

    /**
     * Поиск всех пользователей.
     */
    @Test
    void findAllUsers() {
        int usersNumber = 6;
        createUsers(usersNumber);
        Collection<User> users = userController.findAll();
        assertEquals(usersNumber, users.size(),
                "Список пользователей не читается.");
    }

    /**
     * Добавление нового фильма.
     */
    @Test
    void addNewFilm() {
        Film film = new Film("Testing add film",
                "Testing add film",
                LocalDate.now().minusYears(10),
                120);
        filmController.addNew(film);

        final Film filmFromController = new Film(filmController.getElement(1));

        assertNotNull(filmFromController, "Фильм не читается.");
        assertTrue(filmFromController.getName().equals("Testing add film"),
                "Информация о фильме искажена.");

        assertThrows(ValidationException.class,
                () -> {
                    filmController.addNew(filmFromController);
                },
                "Попытка повторного добавления фильма должна приводить к исключению.");
    }

    /**
     * Добавление нового пользователя.
     */
    @Test
    void addNewUser() {
        User user = new User("user123456@domain",
                "user123456",
                "Testing add user",
                LocalDate.now().minusYears(18));
        userController.addNew(user);

        final User userFromController = userController.getElement(1);
        assertNotNull(userFromController, "Пользователь не читается.");
        assertTrue(userFromController.getEmail().equals("user123456@domain"),
                "Информация о пользователе искажена.");

        assertThrows(ValidationException.class,
                () -> {
                    userController.addNew(userFromController);
                },
                "Попытка повторного добавления пользователя должна приводить к исключению.");
    }

    /**
     * Обновление фильма.
     */
    @Test
    void updateFilm() {
        Film film = new Film("Testing film",
                "Testing update film",
                LocalDate.now().minusYears(10),
                120);
        filmController.addNew(film);

        Film updFilm = new Film("Testing update film",
                "Testing update film",
                LocalDate.now().minusYears(10),
                100);

        updFilm.setId(1000);

        assertThrows(ValidationException.class,
                () -> {
                    filmController.update(updFilm);
                },
                "Попытка обновления несуществующего фильма должна приводить к исключению.");

        updFilm.setId(1);
        filmController.update(updFilm);

        final Film filmFromController = filmController.getElement(1);

        assertNotNull(filmFromController, "Фильм не читается.");
        assertTrue(filmFromController.getName().equals(updFilm.getName()),
                "Информация о фильме не обновляется.");
    }

    /**
     * Обновление пользователя.
     */
    @Test
    void updateUser() {
        User user = new User("user123456@domain",
                "user123456",
                "Testing update user",
                LocalDate.now().minusYears(18));
        userController.addNew(user);

        User updUser = new User("user@update.domain",
                "user098765",
                "Testing update user",
                LocalDate.now().minusYears(18));

        // пытаемся обновить несуществующего пользователя
        updUser.setId(1000);

        assertThrows(ValidationException.class,
                () -> {
                    userController.update(updUser);
                },
                "Попытка обновления несуществующего пользователя должна приводить к исключению.");

        updUser.setId(1);
        userController.update(updUser);

        final User userFromController = userController.getElement(1);
        assertNotNull(userFromController, "Обновленный пользователь не читается.");
        assertTrue(userFromController.getEmail().equals(updUser.getEmail()),
                "Информация о пользователе не обновляется.");
    }

    /**
     * Метод создания тестового массива фильмов
     *
     * @param count - число фильмов для генерации
     */
    private void createFilms(int count) {
        for (int i = 1; i <= count; i++) {
            Film film = new Film("Film №" + i,
                    "testing film " + i,
                    LocalDate.now().minusYears(i),
                    i * 20);
            filmController.addNew(film);
        }
    }

    /**
     * Метод создания тестового массива пользоваелей
     *
     * @param count - число пользователей для генерации
     */
    private void createUsers(int count) {
        for (int i = 1; i <= count; i++) {
            User user = new User("user" + i + "@domain",
                    "user" + i,
                    "testing user" + i,
                    LocalDate.now().minusYears(i * 3));
            userController.addNew(user);
        }
    }
}
