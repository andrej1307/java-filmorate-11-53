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
import ru.yandex.practicum.filmorate.model.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестируем контроллер запросов данных о пользователях
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Удаляем всех пользователей
     */
    @BeforeEach
    void setUp(/*@Autowired MockMvc mvc*/) throws Exception {
        mvc.perform(delete("/users"))
                .andExpect(status().isOk());
    }


    /**
     * Тестируем чтение списка пользователей
     */
    @Test
    void findAllUser() throws Exception {
        addNewUser();

        mvc.perform(get("/users"))
                .andExpect(status().isOk())     // ожидается код статус 200
                .andDo(print());
    }

    /**
     * Тестируем добавление нового пользователя
     */
    @Test
    void addNewUser() throws Exception {
        User user = new User("User1234@domain",
                "user1234", "testing user",
                LocalDate.now().minusYears(22));
        String jsonString = gson.toJson(user);

        // При успешном добавлении пользователя
        // должен возвращаться статус 200 "Ok"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Повторное добавление пользователя
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тестирование обновления сведений о пользователе
     */
    @Test
    void updateUser() throws Exception {
        User user = new User("User1234@domain",
                "user0000", "testing user",
                LocalDate.now().minusYears(22));
        String jsonString = gson.toJson(user);

        // Создаем тестового пользователя
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user.setLogin("user12345");
        user.setName("Updated user.");
        jsonString = gson.toJson(user);

        // Обновление записи без идентификатора
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setId(1000);
        jsonString = gson.toJson(user);
        // Обновление записи c несуществющим идентификатором
        // должно возвращать статус 404 "NotFound"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        user.setId(1);
        jsonString = gson.toJson(user);
        // Успешное обновление записи
        // должно возвращать статус 200 "Ok"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
