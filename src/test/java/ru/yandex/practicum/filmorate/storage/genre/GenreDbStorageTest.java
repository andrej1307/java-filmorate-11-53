package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование справочника жанров фильа
 *
 * Для успешного выполнения тестов, при инициализации базы данных
 * должндолжен быть полностью заполнен справочник жанров.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;
    private static List<Genre> testGenres = new ArrayList<>();

    /**
     * Инициализация эталонного списка жанров.
     */
    @BeforeAll
    static void setUp() {
        testGenres.add(new Genre(1, "Комедия"));
        testGenres.add(new Genre(2, "Драма"));
        testGenres.add(new Genre(3, "Мультфильм"));
        testGenres.add(new Genre(4, "Триллер"));
        testGenres.add(new Genre(5, "Документальный"));
        testGenres.add(new Genre(6, "Боевик"));
    }

    /**
     * Тестирование списка жанров
     */
    @Test
    void findAllGenres() {
        Collection<Genre> genres = genreDbStorage.findAllGenres();
        for (Genre genre : testGenres) {
            assertTrue(genres.contains(genre),
                    "В базе данных отсутствует " + genre.toString());
        }
    }

    /**
     * Тестирование поиска жанров по идентификатору
     */
    @Test
    void findGenre() {
        for (Genre genre : testGenres) {
            Optional<Genre> genreOptional = genreDbStorage.findGenre(genre.getId());

            assertThat(genreOptional)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(genre);
        }
    }

}