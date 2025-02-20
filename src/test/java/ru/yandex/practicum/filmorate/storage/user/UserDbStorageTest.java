package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование Хранилища пользователей в базе данных
 *
 * Для успешного выполнения тестов, при инициализации базы данных
 * должна быть подготовлена информация о четырех тестовых пользователях.
 * Файл первоначальных данных ./src/test/resources/data.sql
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
     * Чтение информации о тестовом пользователе по заданному идентификатору.
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
        assertNotNull(userDb.getId(),
                "addNewUser() - При добавлении пользователя в базу должен быть присвоен не нулевой идентификатор");

        Optional<User> userOptional = userDbStorage.getUserById(userDb.getId());

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    /**
     * Тестирование поиска информации о всех пользователях в базе.
     */
    @Test
    void findAllUsers() {
        Collection<User> users = userDbStorage.findAllUsers();

        assertTrue(users.size() > 0,
                "findAllUsers() - В базе данных отсутствует информация о пользователях.");
    }

    /**
     * Тестирование обновления информации о тестовом пользователе.
     */
    @Test
    void updateUser() {
        User userUpdate = getTestUser();
        userUpdate.setEmail("updated_user@test.com");

        userDbStorage.updateUser(userUpdate);

        Optional<User> userOptional = userDbStorage.getUserById(userUpdate.getId());

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userUpdate);
    }

    /**
     * Тестирование добавления "друга"
     */
    @Test
    void addFriend() {
        final int userId = TEST_USER_ID;
        final int friendId = 2;

        Optional<User> userOptional = userDbStorage.getUserById(userId);
        assertThat(userOptional)
                .withFailMessage("addFriend() - Не определен пользователь id=%s для создания \"друзей\".", userId)
                .isPresent();
        if (userOptional.isEmpty()) return;

        userOptional = userDbStorage.getUserById(friendId);
        assertThat(userOptional)
                .withFailMessage("addFriend() - Не определен пользователь id=%s в качестве нового \"друга\".", friendId)
                .isPresent();
        if (userOptional.isEmpty()) return;
        User friend = userOptional.get();

        userDbStorage.addFriend(userId, friendId);
        Collection<User> friends = userDbStorage.getUserFriends(userId);

        assertTrue(friends.contains(friend),
                "addFriend() - Пользователь id=" + userId + ". не удалось добавить друга id=" + friendId);
    }

    /**
     * Тестирование списка друзей заданного пользователя
     */
    @Test
    void getUserFriends() {
        userDbStorage.addFriend(TEST_USER_ID, 2);
        userDbStorage.addFriend(TEST_USER_ID, 3);
        userDbStorage.addFriend(TEST_USER_ID, 4);

        Collection<User> friends = userDbStorage.getUserFriends(TEST_USER_ID);
        assertTrue(friends.size() > 0,
                "getUserFriends() - Список друзй пользователя id="
                        + TEST_USER_ID + " не найден.");
        assertTrue(friends.size() == 3,
                "getUserFriends() - Количество друзй пользователя id="
                        + TEST_USER_ID + " не соответствует ожидаемому.");
    }

    /**
     * Тестирование удаления пользователя из друзей
     */
    @Test
    void removeFriend() {
        final int deletedFriendId = 3;
        getUserFriends();

        userDbStorage.breakUpFriends(TEST_USER_ID, deletedFriendId);
        Collection<User> friends = userDbStorage.getUserFriends(TEST_USER_ID);

        Optional<User> friend = friends.stream()
                .filter(user -> user.getId() == deletedFriendId)
                .findFirst();

        assertThat(friend)
                .withFailMessage("removeFriend() - пользователь id=%s не был удален из \"друзей\"%s.",
                        deletedFriendId, TEST_USER_ID)
                .isEmpty();
    }

    /**
     * Тестирование поиска общих друзей
     */
    @Test
    void findCommonFriends() {
        getUserFriends();
        userDbStorage.addFriend(3, 1);
        userDbStorage.addFriend(3, 4);

        Collection<User> friends = userDbStorage.getCommonFriends(TEST_USER_ID, 3);
        Optional<User> commonFriend = friends.stream().findFirst();

        assertThat(commonFriend)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 4));
    }

    /**
     * Тестирование удаления пользователей
     */
    @Test
    void removeAllUsers() {
        addNewUser();

        userDbStorage.removeAllUsers();
        Collection<User> users = userDbStorage.findAllUsers();

        assertTrue(users.size() == 0,
                "removeAllUsers() - Не удалось удалить всех пользователей.");
    }
}