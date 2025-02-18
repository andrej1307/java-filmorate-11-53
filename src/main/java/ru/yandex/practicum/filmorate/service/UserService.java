package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

/**
 * Класс реализации запросов к информации о пользователях
 */
@Slf4j
@Service
public class UserService {

    private final UserStorage users;

    public UserService(@Qualifier("userDbStorage") UserStorage users) {
        this.users = users;
    }

    /**
     * Метод поиска всех пользователей
     *
     * @return - список пользователей
     */
    public Collection<User> findAllUsers() {
        return users.findAllUsers();
    }

    /**
     * Метод добавления нового пользователя.
     *
     * @param user - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    public User addNewUser(User user) {
        // "имя для отображения может быть пустым
        // — в таком случае будет использован логин" (ТЗ-№10)
        if (user.getName() == null | user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.findAllUsers().contains(user)) {
            throw new ValidationException("Пользователь уже существует "
                    + user.getEmail());
        }
        return users.addNewUser(user);
    }

    /**
     * Метод чтения информации о пользователе по заданному идентификатору
     *
     * @param id - идентификатор пользователя
     * @return - найденный объект
     */
    public User getUserById(Integer id) {
        User user = users.getUserById(id).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id));
        return user;
    }

    /**
     * Метод обновления информации о пользователе.
     * При вызове метода промзводится проверка аннотаций только для маркера OnUpdate.class.
     * Кроме id любой другой параметр может отсутствовать
     *
     * @param updUser - объект с обновленной информацией о пользователе
     * @return - обновленный объект
     */
    public User updateUser(User updUser) {
        Integer id = updUser.getId();
        User user = users.getUserById(id).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id));

        // Обновляем информаию во временном объекте
        if (updUser.getEmail() != null) {
            user.setEmail(updUser.getEmail());
        }
        if (updUser.getLogin() != null) {
            user.setLogin(updUser.getLogin());
        }
        if (updUser.getName() != null) {
            user.setName(updUser.getName());
        }
        if (updUser.getBirthday() != null) {
            user.setBirthday(updUser.getBirthday());
        }

        users.updateUser(user);
        return user;
    }

    /**
     * Удаление всех пользователей
     *
     * @return - сообщение о выполнении
     */
    public String removeAllUsers() {
        log.debug("Sevice: Удаляем всех пользователей.");
        users.removeAllUsers();
        return "Все пользователи удалены.";
    }

    /**
     * Медод добавления пользователей в друзья
     * добавление в друзья происходит взаимное без подтверждений
     *
     * @param id1 - идентификатор пользователя
     * @param id2 - идентификатор друга
     */
    public void addFriends(Integer id1, Integer id2) {
        users.getUserById(id1).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id1));
        users.getUserById(id2).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id2));

        // Добавление в друзья
        users.addFriend(id1, id2);
    }

    /**
     * Метод удаления пользователя из "друзей"
     *
     * @param id1 - идентификатор пользователя
     * @param id2 - идентификатор друга
     * @return - сообщение о подтверждении
     */
    public void breakUpFriends(Integer id1, Integer id2) {
        users.getUserById(id1).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id1));
        users.getUserById(id2).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id2));

        users.breakUpFriends(id1, id2);
    }

    /**
     * Поиск всех друзей пользователя
     *
     * @param userId - идентификатор пользователя
     * @return - список друзей
     */
    public Collection<User> getUserFriends(Integer userId) {
        users.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + userId));

        return users.getUserFriends(userId);
    }

    /**
     * Поиск общих друзей пользователей
     *
     * @param id1 - идентификатор пользователя
     * @param id2 - идентификатор другого пользователя
     * @return - список общих друзей
     */
    public Collection<User> getCommonFriends(Integer id1, Integer id2) {
        users.getUserById(id1).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id1));
        users.getUserById(id2).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + id2));

        return users.getCommonFriends(id1, id2);
    }

    public String getDdInfo() {
        return users.getDbInfo();
    }

}
