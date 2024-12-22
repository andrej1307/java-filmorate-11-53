package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестируем контроллер запросов о фильмах
 */
@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    MockMvc mvc;

    static Gson gson = new GsonBuilder()
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

        mvc.perform(delete("/users"))
                .andExpect(status().isOk());

        // Создадим одного пользователя для "лайков"
        User user = new User("User1234@domain",
                "user1234", "test user",
                LocalDate.now().minusYears(22));

        String jsonString = gson.toJson(user);
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Тестируем режим поиска фильмов.
     */
    @Test
    void findAllFilms() throws Exception {
        makeFilms(3);
        mvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем добавление информации о новом фильме.
     */
    @Test
    void addNewFilm() throws Exception {
        Film film = new Film("Film Test1",
                "Testing addNewFilm",
                LocalDate.now().minusYears(10),
                60, 0);
        String jsonString = gson.toJson(film);

        // При успешном добавлении фильма
        // должен возвращаться статус 200 "Ok"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // При повторном добавлении фильма
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тестируем обновление информации о фильме
     */
    @Test
    void updateFilm() throws Exception {
        Film film = new Film("Film Test2",
                "Testing updateFilm",
                LocalDate.now().minusYears(10),
                60, 0);
        String jsonString = gson.toJson(film);

        // Добавляем тестовый фильм
        mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setDescription("Updated.");
        jsonString = gson.toJson(film);
        // При обновлении фильма с отсутствующим id
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(put("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        film.setId(1000);
        jsonString = gson.toJson(film);
        // При обновлении фильма с неверным id
        // должен возвращаться статус 404 "NotFound"
        mvc.perform(put("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        film.setId(1);
        jsonString = gson.toJson(film);
        // При обновлении фильма с корректным id
        // должен возвращаться статус 200 "Ok"
        mvc.perform(put("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем добавление "лайка"
     *
     * @throws Exception
     */
    @Test
    void addLike() throws Exception {
        makeFilms(3);


        // При добавлении "лайка" от несуществующего пользователя
        // должен возвращаться статус 404 "NotFound"
        mvc.perform(put("/films/1/like/1000")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // При добавлении "лайка"
        // должен возвращаться статус 200 "Ok"
        mvc.perform(put("/films/2/like/1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем удаление "лайка"
     *
     * @throws Exception
     */
    @Test
    void deleteLike() throws Exception {
        addLike();

        // При удалении "лайка"
        // должен возвращаться статус 200 "Ok"
        mvc.perform(delete("/films/2/like/1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Генерация тестовых фильмов
     *
     * @param count - количество фильмов
     * @throws Exception
     */
    void makeFilms(int count) throws Exception {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append("{\"name\": \"Film%d\",");
        fBuilder.append("\"description\": \"description%d\",");
        fBuilder.append("\"releaseDate\": \"2000-01-%02d\",");
        fBuilder.append("\"duration\": %d}");
        String formatStr = fBuilder.toString();

        for (int i = 1; i <= count; i++) {
            String jsonString = String.format(formatStr, i, i, i, i * 10);
            mvc.perform(post("/films")
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }

    }
}
