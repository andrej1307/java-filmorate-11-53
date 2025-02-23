package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестирование Хранилища Фильмов в базе данных
 * <p>
 * Для успешного выполнения тестов, при инициализации базы данных
 * должна быть подготовлена информация о четырех тестовых фильмах.
 * Для фильма с id=TEST_FILM_ID нужно создать запись в таблице жанров.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    public static final int TEST_FILM_ID = 1;

    private final FilmDbStorage filmDbStorage;

    /**
     * Генерация информации о тестовом фильме
     * Поля должны соответствовать содержимому базы данных для фильма TEST_FILM_ID
     *
     * @return - объект, который ожидается для TEST_FILM_ID
     */
    static Film getTestFilm() {
        Film film = new Film();
        film.setId(TEST_FILM_ID);
        film.setName("TestFilmName");
        film.setDescription("TestFilmDescription");
        film.setReleaseDate(LocalDate.of(2001, 2, 3));
        film.setDuration(51);
        film.setMpa(new Mpa(1));
        film.addGenre(new Genre(1, "Комедия"));
        return film;
    }

    /**
     * Тестирование добавления информации о новом фильме
     */
    @Test
    void addNewFilm() {
        Film film = new Film();
        film.setName("Фильм! Фильм! Фильм!");
        film.setDescription("Юмористический рссказ о том, как делают кино.");
        film.setReleaseDate(LocalDate.of(1968, 9, 1));
        film.setDuration(20);
        film.setMpa(new Mpa(1));
        film.addGenre(new Genre(1, "Комедия"));
        film.addGenre(new Genre(3, "Мультфильм"));

        Film filmDb = filmDbStorage.addNewFilm(film);
        assertNotNull(filmDb.getId(),
                "При добавлении нового фильа должен быть присвоен ненулевой идентификатор.");

        // Сравниваем исходный фильм с сохраненным по всем полям
        Optional<Film> filmOptional = filmDbStorage.getFilmById(filmDb.getId());
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(filmDb);
    }

    /**
     * Тестирование чтения информации о фильме по заданному идентификатору
     */
    @Test
    void getFilmById() {
        Film film = getTestFilm();
        Optional<Film> filmOptional;

        // Попытка чтения несушествующего фильма
        filmOptional = filmDbStorage.getFilmById(10000);
        assertThat(filmOptional)
                .withFailMessage("При чтении несуществующего фильма должен возвращаться пустой объект")
                .isEmpty();

        filmOptional = filmDbStorage.getFilmById(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .get()
                .isEqualTo(film);
    }

    /**
     * Тестирование списка фильмов
     */
    @Test
    void findAllFilms() {
        Collection<Film> films = filmDbStorage.findAllFilms();
        assertTrue(films.size() > 0,
                "findAllFilms() - В базе данных отсутствует информация о фильмах.");
    }

    /**
     * Тестирование расчета популярности фильмов
     */
    @Test
    void findPopularFilms() {
        // задаем "лайки" к фильмам
        filmDbStorage.addNewLike(1, 1);
        filmDbStorage.addNewLike(2, 1);
        filmDbStorage.addNewLike(2, 2);
        filmDbStorage.addNewLike(3, 1);
        filmDbStorage.addNewLike(3, 2);
        filmDbStorage.addNewLike(3, 3);
        filmDbStorage.addNewLike(3, 4);
        filmDbStorage.addNewLike(4, 4);
        filmDbStorage.addNewLike(4, 2);
        filmDbStorage.addNewLike(4, 3);

        Collection<Film> films = filmDbStorage.findPopularFilms(2);
        List<Film> popular = new LinkedList<>(films);
        assertEquals(popular.get(0), filmDbStorage.getFilmById(3).get(),
                "Самый популярный фильм расчитан неверно.");

        assertEquals(popular.get(1), filmDbStorage.getFilmById(4).get(),
                "Второй по популярности фильм расчитан неверно.");
    }

    /**
     * Тестирование обновления информации о фильме
     */
    @Test
    void updateFilm() {
        Film film = getTestFilm();
        film.setName("filmNameUpdated");
        film.setReleaseDate(LocalDate.of(1999, 12, 31));
        film.addGenre(new Genre(6, "Боевик"));

        filmDbStorage.updateFilm(film);

        Optional<Film> filmOptional = filmDbStorage.getFilmById(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .get()
                .isEqualTo(film);
    }

    /**
     * Тестирование добавления "лайка" к фильму
     */
    @Test
    void addNewLike() {
        Film film = getTestFilm();
        film.setId(null);
        film.setName("TestLikeFilmName");
        film.setDescription("TestLikeFilmDescription");

        Film filmDb = filmDbStorage.addNewFilm(film);

        Integer rank = filmDbStorage.addNewLike(filmDb.getId(), 1);
        assertEquals(1, rank, "При добавлении \"лйка\" произошла ошибка.");

        rank = filmDbStorage.addNewLike(filmDb.getId(), 3);
        assertEquals(2, rank, "При подсчете \"лйков\" произошла ошибка.");

        rank = filmDbStorage.addNewLike(filmDb.getId(), 3);
        assertEquals(2, rank, "При добавлении повторного \"лйка\" произошла ошибка счетчика.");
    }

    /**
     * Тестирование удаления "лайка у фильма"
     */
    @Test
    void removeLike() {
        final int userId = 1;

        Integer expectedRank = filmDbStorage.addNewLike(TEST_FILM_ID, userId) - 1;
        Integer rank = filmDbStorage.removeLike(TEST_FILM_ID, userId);

        assertEquals(expectedRank, rank,
                "При удалении \"лайка\" произошла ошибка.");
    }

    /**
     * Тестирование Удаления информации о всех фильмах
     */
    @Test
    void removeAllFilms() {
        Film film = getTestFilm();
        film.setId(null);
        film.setName("TestFilmNameForRmove");
        filmDbStorage.addNewFilm(film);

        filmDbStorage.removeAllFilms();
        Collection<Film> films = filmDbStorage.findAllFilms();
        assertTrue(films.size() == 0,
                "При удалении фильмов произошла ошибка.");
    }
}