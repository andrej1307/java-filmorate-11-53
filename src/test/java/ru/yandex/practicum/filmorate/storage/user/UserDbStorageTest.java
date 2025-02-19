package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тестирование Хранилища пользователей в базе данных
 */
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    public static final int TEST_USER_ID = 1;

    /**
     * Генерация тестового пользователя
     *
     * @return - Optional<User> информация о пользователе если найден
     */
    static User getTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setName("testName");
        user.setBirthday(LocalDate.of(2001, 9, 22));
        return user;
    }

    /**
     * Тест чтения информации о тестовом пользователе.
     * Данные должны быть подготовлены заранее,
     * при инициализации базы данных скриптом data.sql
     */
    @Test
    void getUserById() {
        User user = getTestUser();
        Optional<User> userOptional = userDbStorage.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user);

    }

    /**
     * Тест добавления в базу данных нового пользователя
     */
    @Test
    void addNewUser() {
        User user = new User();
        user.setEmail("test@user.test");
        user.setName("TesstUserName");
        user.setLogin("TestUserLogin");
        user.setBirthday(LocalDate.of(2001, 7, 22));

        User userDb = userDbStorage.addNewUser(user);
        assertNotNull(userDb, "UserDb should not be null");
        assertNotNull(userDb.getId(), "UserDb id should not be null");
    }

}