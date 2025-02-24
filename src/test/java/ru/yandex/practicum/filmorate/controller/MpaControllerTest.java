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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестирование контроллера запросов работы со справочником жанров рейтингов MPA
 * <p>
 * Для успешного выполнения тестов, при инициализации базы данных
 * должндолжен быть полностью заполнен справочник рейтингов.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaControllerTest {
    private static List<Mpa> testMpaList = new ArrayList<>();

    private static final List<Genre> testGenres = new ArrayList<>();

    @Autowired
    MockMvc mvc;

    // Определяем тип сериализации списка
    class MpaListTypeToken extends TypeToken<List<Mpa>> {
    }

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Инициализация эталонного списка рейтингов.
     */
    @BeforeAll
    static void setUp() {
        testMpaList.add(new Mpa(1, "G", "у фильма нет возрастных ограничений"));
        testMpaList.add(new Mpa(2, "PG", "детям рекомендуется смотреть фильм с родителями"));
        testMpaList.add(new Mpa(3, "PG-13", "детям до 13 лет просмотр не желателен"));
        testMpaList.add(new Mpa(4, "R", "лицам до 17 лет просматривать фильм можно только в присутствии взрослого"));
        testMpaList.add(new Mpa(5, "NC-17", "лицам до 18 лет просмотр запрещён"));
    }

    /**
     * Тестируем список всех рейтингов
     */
    @Test
    void findAllMpa() throws Exception {
        MvcResult result = mvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andReturn();

        List<Mpa> mpas = gson.fromJson(result.getResponse().getContentAsString(),
                new MpaListTypeToken().getType());
        for (Mpa mpa : mpas) {
            assertTrue(testMpaList.contains(mpa),
                    "Получен неизвестный рейтинг: " + mpa.toString());
        }
    }

    /**
     * Тестируем поиск рейтинга по идентификаторам
     *
     * @throws Exception
     */
    @Test
    void findMpaById() throws Exception {
        for (Mpa mpa : testMpaList) {
            MvcResult result = mvc.perform(get("/mpa/" + mpa.getId()))
                    .andExpect(status().isOk())
                    .andReturn();
            Mpa mpaDb = gson.fromJson(result.getResponse().getContentAsString(), Mpa.class);
            assertTrue(mpa.equals(mpaDb),
                    "Получен неизвестный рейтинг: " + mpaDb.toString());
        }
    }

}