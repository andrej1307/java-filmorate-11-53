package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.LocalDateAdapter;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестируем контроллер запросов работы с данными о пользователях
 * <p>
 * Для успешного выполнения тестов, при инициализации базы данных
 * должна быть подготовлена информация о четырех тестовых пользователях.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // Определяем тип сериализации списка
    class UserListTypeToken extends TypeToken<List<User>> {
    }

    /**
     * Тестируем чтение списка пользователей
     */
    @Test
    void findAllUser() throws Exception {
        MvcResult result = mvc.perform(get("/users"))
                .andExpect(status().isOk())      // ожидается код статус 200
                .andReturn();
        List<User> users = gson.fromJson(result.getResponse().getContentAsString(), new UserListTypeToken().getType());
        assertTrue(!users.isEmpty(),
                "Список пользователей пуст.");
    }

    /**
     * Тестируем добавление нового пользователя
     */
    @Test
    void addNewUser() throws Exception {
        User user = new User();
        user.setLogin("user1234");
        user.setName("testUserName");
        user.setBirthday(LocalDate.now().minusYears(22));

        user.setEmail("User1234_domain@");
        String jsonString = gson.toJson(user);
        // При добавлении пользователя некорректным Email
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user.setEmail("User1234@domain");
        jsonString = gson.toJson(user);
        // При успешном добавлении пользователя
        // должен возвращаться статус 201 "Created"
        MvcResult result = mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // Сохраняем созданного пользователя
        User userDb = gson.fromJson(result.getResponse().getContentAsString(), User.class);
        assertNotNull(userDb.getId(),
                "При добавлении пользователя должен быть присвоен ненулевой идентификатор");

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
        User user = new User();
        user.setEmail("UserUpdate@domain");
        user.setLogin("userUpdate");
        user.setName("testUpdateUserName");
        user.setBirthday(LocalDate.now().minusYears(22));
        String jsonString = gson.toJson(user);

        // При успешном добавлении пользователя
        // должен возвращаться статус 201 "Created"
        MvcResult result = mvc.perform(post("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // Сохраняем созданного пользователя
        User userDb = gson.fromJson(result.getResponse().getContentAsString(), User.class);

        // готовим данные для обновления
        user.setLogin("userUpd12345");
        user.setName("Updated user.");
        user.setBirthday(LocalDate.now().minusYears(22));

        jsonString = gson.toJson(user);
        // Обновление записи без идентификатора пользователя
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

        user.setId(userDb.getId());
        jsonString = gson.toJson(user);
        // Успешное обновление записи
        // должно возвращать статус 200 "Ok"
        result = mvc.perform(put("/users")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Сохраняем пользователя из ответа после обновления
        userDb = gson.fromJson(result.getResponse().getContentAsString(), User.class);

        assertThat(userDb)
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    /**
     * Тестирование добавления "друга"
     *
     * @throws Exception
     */
    @Test
    void addFriends() throws Exception {
        // Объявление в "друзья" несуществующего пользователя
        // должно возвращать статус 404 "NotFound"
        mvc.perform(put("/users/1000/friends/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Объявление в "друзья" несуществующего друга
        // должно возвращать статус 404 "NotFound()"
        mvc.perform(put("/users/1/friends/1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Объявление в "друзья" сущществующих пользователей
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестирование удаления пользователя из друзей
     *
     * @throws Exception
     */
    @Test
    void breakUpFriends() throws Exception {
        // Добавление в "друзья" пользователея
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Удаление из "друзьей"  не сущществующих пользователей
        // должно возвращать статус 404 "NotFound"
        mvc.perform(delete("/users/1/friends/1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Удаление из "друзьей" сущществующих пользователей
        // должно возвращать статус 200 "Ok"
        mvc.perform(delete("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестирование поиска друзей пользователя
     *
     * @throws Exception
     */
    @Test
    void findUsersFriends() throws Exception {
        // Добавление в "друзья" пользователея
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/4/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Добавление в "друзья" пользователея
        // должно возвращать статус 200 "ok"
        mvc.perform(put("/users/4/friends/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // читаем список "друзей", несуществующего пользователя
        // должно возвращать статус 404 "NotFound"
        mvc.perform(get("/users/2000/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // читаем список "друзей"
        // должно возвращать статус 200 "ok"
        MvcResult result = mvc.perform(get("/users/4/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<User> users = gson.fromJson(result.getResponse().getContentAsString(), new UserListTypeToken().getType());
        assertTrue(users.size() == 2,
                "Количество найденых \"друзей\" не соответствует ожидаемому.");
    }

    /**
     * Тестирование поиска общих друзей у пользователей
     *
     * @throws Exception
     */
    @Test
    void findCommonFriends() throws Exception {
        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put("/users/3/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put("/users/3/friends/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put("/users/4/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put("/users/4/friends/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // читаем список общих "друзей"
        // должно возвращать статус 200 "ok"
        MvcResult result = mvc.perform(get("/users/1/friends/common/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<User> users = gson.fromJson(result.getResponse().getContentAsString(), new UserListTypeToken().getType());
        assertTrue(users.size() == 2,
                "Количество общих \"друзей\" не соответствует ожидаемому.");
    }

}