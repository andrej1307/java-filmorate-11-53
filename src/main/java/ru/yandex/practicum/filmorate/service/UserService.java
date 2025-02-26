package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> findAllUsers();

    User addNewUser(User user);

    User getUserById(Integer id);

    User updateUser(User updUser);

    String removeAllUsers();

    void addFriends(Integer id1, Integer id2);

    void breakUpFriends(Integer id1, Integer id2);

    Collection<User> getUserFriends(Integer userId);

    Collection<User> getCommonFriends(Integer id1, Integer id2);

}
