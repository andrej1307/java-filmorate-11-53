package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    // добавление нового пользователя
    User addNewUser(User newUser);

    // чтение пользователя по идентификатору
    Optional<User> getUserById(Integer id);

    // чтение всех пользователей
    Collection<User> findAllUsers();

    void updateUser(User updUser);

    void removeAllUsers();

    void addFriend(Integer userId, Integer friendId);

    void breakUpFriends(Integer id1, Integer id2);

    Collection<User> getUserFriends(Integer userId);

    Collection<User> getCommonFriends(Integer id1, Integer id2);
}
