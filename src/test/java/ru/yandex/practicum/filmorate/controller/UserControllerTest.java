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
     * Перед каждым тестом удаляем всех пользователей
     */
    @BeforeEach
    void setUp() throws Exception {
        mvc.perform(delete("/users"))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем чтение списка пользователей
     */
    @Test
    void findAllUser() throws Exception {
        makeUsers(3);

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
                "user1234", "test user",
                LocalDate.now().minusYears(22));
        String jsonString = gson.toJson(user);

        // При успешном добавлении пользователя
        // должен возвращаться статус 200 "Ok"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

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
                .andExpect(status().isCreated());

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

    /**
     * Тестируем добавление друзей
     *
     * @throws Exception
     */
    @Test
    void addFriends() throws Exception {
        makeUsers(3);

        // Объявление в "друзья" несуществующего пользователя
        // должно возвращать статус 404 "NotFound"
        mvc.perform(put("/users/1000/friends/1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Объявление в "друзья" несуществующего друга
        // должно возвращать статус 404 "NotFound()"
        mvc.perform(put("/users/1/friends/1000")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Объявление в "друзья" сущществующих пользователей
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/1/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Объявление в "друзья" сущществующих пользователей (граничный случай)
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/3/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем удаление друзей
     *
     * @throws Exception
     */
    @Test
    void removeFriends() throws Exception {
        addFriends();

        // Удаление из "друзьей"  не сущществующих пользователей
        // должно возвращать статус 404 "NotFound"
        mvc.perform(delete("/users/1/friends/1000")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Удаление из "друзьей" сущществующих пользователей
        // должно возвращать статус 200 "Ok"
        mvc.perform(delete("/users/1/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем чтение списка друзей
     *
     * @throws Exception
     */
    @Test
    void getFriends() throws Exception {
        makeUsers(3);

        // Объявление в "друзья"
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/1/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Объявление в "друзья"
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/3/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // читаем список "друзей", несуществующего пользователя
        // должно возвращать статус 404 "NotFound"
        mvc.perform(get("/users/2000/friends")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


        // читаем список "друзей"
        // должно возвращать статус 200 "ok"
        mvc.perform(get("/users/2/friends")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем поиск общих друзей
     *
     * @throws Exception
     */
    @Test
    void findCommonFrends() throws Exception {
        makeUsers(3);

        // Объявление в "друзья"
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/1/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Объявление в "друзья"
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/3/friends/2")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // читаем список общих "друзей"
        // должно возвращать статус 200 "ok"
        mvc.perform(get("/users/1/friends/common/3")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Создание тестовых пользователей
     *
     * @param count - требуемое клличество тестовых пользователей
     * @throws Exception
     */
    void makeUsers(int count) throws Exception {
        StringBuilder fBuilder = new StringBuilder();
        fBuilder.append("{\"email\": \"user000%d@domain\",");
        fBuilder.append("\"login\": \"USER000%d\",");
        fBuilder.append("\"name\": \"userName00%d\",");
        fBuilder.append("\"birthday\": \"2000-01-%02d\"}");
        String formatStr = fBuilder.toString();

        for (int i = 1; i <= count; i++) {
            String jsonString = String.format(formatStr, i, i, i, i);
            // При успешном добавлении пользователя
            // должен возвращаться статус 200 "Ok"
            mvc.perform(post("/users")
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }
    }
}
