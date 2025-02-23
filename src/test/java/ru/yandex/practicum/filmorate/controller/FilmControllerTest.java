package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.LocalDateAdapter;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Autowired
    MockMvc mvc;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Тестируем поиск всех фильмов
     */
    @Test
    void findAllFilms()  throws Exception {
        MvcResult result = mvc.perform(get("/films"))
                .andExpect(status().isOk())      // ожидается код статус 200
                .andReturn();
        List<Film> films = gson.fromJson(result.getResponse().getContentAsString(), List.class);
        assertTrue(!films.isEmpty(),
                "Список фильмов пуст.");
    }

    @Test
    void findFilm() {
    }

    @Test
    void findPopularFilms() {
    }

    @Test
    void addNewFilm() {
    }

    @Test
    void updateFilm() {
    }

    @Test
    void addLike() {
    }

    @Test
    void removeLike() {
    }
}