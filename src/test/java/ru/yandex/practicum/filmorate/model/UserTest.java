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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестирование ограничений на значения полей класса User.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserTest {
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
     * Тестирование email пользователя
     */
    @Test
    void testEmail() throws Exception {
        User user = new User(null,
                "userTest",
                "Testing user",
                LocalDate.now().minusYears(32));
        String jsonString = gson.toJson(user);

        // Создание пользователя без email
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setEmail("user.domain@");
        jsonString = gson.toJson(user);
        // Создание пользователя с неправильным email
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setEmail("user@domain");
        jsonString = gson.toJson(user);
        // Создание пользователя с корректным email
        // должно возвращать статус 200 "Ok"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестирование login пользователя
     */
    @Test
    void testLogin() throws Exception {
        User user = new User("user1234@test",
                "",
                "Testing user",
                LocalDate.now().minusYears(32));
        String jsonString = gson.toJson(user);

        // Создание пользователя с пустым login
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setLogin("user test");
        jsonString = gson.toJson(user);
        // Создание пользователя с login содержащим пробел
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setLogin("user1234");
        jsonString = gson.toJson(user);
        // Создание пользователя с корректным login (содержит только латинские буквы и цифры)
        // должно возвращать статус 200 "Ok"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем корректность даты рождения
     */
    @Test
    void testBirthday() throws Exception {
        User user = new User("user1234@test",
                "user1234",
                "Testing user",
                LocalDate.now().plusDays(30));

        String jsonString = gson.toJson(user);
        // Создание пользователя с датой рождения в будущем
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setBirthday(LocalDate.now().minusYears(30));
        jsonString = gson.toJson(user);
        // Создание тестового пользователя с корректной датой рождения
        // должно возвращать статус 200 "Ok"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем группу аннотаций для режима обновления данных
     */
    @Test
    void testUpdateUser() throws Exception {
        User user = new User("user1234@test",
                "user1234",
                "Testing user",
                LocalDate.now().minusYears(32));
        String jsonString = gson.toJson(user);
        // Создание тестового пользователя
        // должно возвращать статус 200 "Ok"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        jsonString = "{\"id\": 1, \"email\": \"user.domain@\"}";
        // Изменение пользователю email на некорректный
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        jsonString = "{\"id\": 1, \"email\": \"user@host.domain\"}";
        // Изменение пользователю email на допустимый
        // должно возвращать статус 200 "Ok"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        jsonString = "{\"id\": 1, \"login\": \"user test12\"}";
        // Изменение пользователю login на некорректный
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        jsonString = "{\"id\": 1, \"login\": \"userTest\"}";
        // Изменение пользователю login на допустимый
        // должно возвращать статус 200 "Ok"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        jsonString = "{\"id\": 1, \"birthday\": \"2050-01-01\"}";
        // Обновление пользователя с датой рождения в будущем
        // должно возвращать статус 400 "BadRequest"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        jsonString = "{\"id\": 1, \"birthday\": \"2005-01-01\"}";
        // Обновление пользователя корректной датой рождения
        // должно возвращать статус 200 "Ok"
        mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
