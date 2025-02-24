package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестирование контроллера запросов работы со справочником жанров фильа
 * <p>
 * Для успешного выполнения тестов, при инициализации базы данных
 * должндолжен быть полностью заполнен справочник жанров.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest {

    private static final List<Genre> testGenres = new ArrayList<>();

    @Autowired
    MockMvc mvc;

    // Определяем тип сериализации списка
    class GenreListTypeToken extends TypeToken<List<Genre>> {
    }

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

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
     * Тестируем список всех жанров
     */
    @Test
    void findAllGenres() throws Exception {
        MvcResult result = mvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andReturn();

       List<Genre> genres = gson.fromJson(result.getResponse().getContentAsString(),
               new GenreListTypeToken().getType());
       for (Genre genre : genres) {
            assertTrue(testGenres.contains(genre),
                    "Получен неизвестный жанр: " + genre.toString());
        }
    }

    /**
     * Тестируем поиск жанра по идентификатору
     */
    @Test
    void findGenreById() throws Exception {
        for (Genre genre : testGenres) {
            MvcResult result = mvc.perform(get("/genres/" + genre.getId()))
                    .andExpect(status().isOk())
                    .andReturn();
            Genre genreDb = gson.fromJson(result.getResponse().getContentAsString(), Genre.class);
            assertTrue(genre.equals(genreDb),
                    "Получен неизвестный жанр: " + genreDb.toString());
        }
    }
}