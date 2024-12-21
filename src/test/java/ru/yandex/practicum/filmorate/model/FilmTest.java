package ru.yandex.practicum.filmorate.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестирование ограничений на значения полей класса Film
 */
@SpringBootTest
@AutoConfigureMockMvc
class FilmTest {
    @Autowired
    MockMvc mvc;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Перед каждым тестом очищаем список фильмов.
     */
    @BeforeEach
    void setUp() throws Exception {
        mvc.perform(delete("/films"))
                .andExpect(status().isOk());
    }

    /**
     * Проверка непустого названия фильма.
     */
    @Test
    void testName() throws Exception {
        Film film = new Film("",
                "Testing film.name",
                LocalDate.now().minusYears(10),
                60);
        String jsonString = gson.toJson(film);
        // При добавлении фильма без названия
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    /**
     * Проверка допусимого размера описания.
     */
    @Test
    void testDescription() throws Exception {
        Film film = new Film("Film",
                "12345678901234567890123456789012345678901234567890"
                        + "12345678901234567890123456789012345678901234567890"
                        + "12345678901234567890123456789012345678901234567890"
                        + "12345678901234567890123456789012345678901234567890"
                        + "12345678901234567890123456789012345678901234567890",
                LocalDate.now().minusYears(10),
                60);
        String jsonString = gson.toJson(film);
        // При добавлении фильма
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    /**
     * Проверка допустимой даты выпуска фильма
     */
    @Test
    void testReleaseDate() throws Exception {
        Film film = new Film("Film",
                "Testing film.releaseDate",
                LocalDate.now().plusDays(1),
                60);
        String jsonString = gson.toJson(film);
        // При добавлении фильма
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        jsonString = gson.toJson(film);
        // При добавлении фильма
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    /**
     * Проверка допустимой длительности фильма
     */
    @Test
    void testDuration() throws Exception {
        Film film = new Film("Film",
                "Testing film.releaseDate",
                LocalDate.now().minusYears(10),
                0);
        String jsonString = gson.toJson(film);
        // При добавлении фильма
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}
