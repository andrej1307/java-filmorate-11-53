package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс реализации запросов к информации о пользователях
 */
@Slf4j
@Service
public class UserService {

    static protected UserStorage users = Storages.getUerStorage();

    /**
     * Метод поиска всех пользователей
     *
     * @return - список пользователей
     */
    public Collection<User> findAllUsers() {
        log.debug("Sevice: Get all users {}.", users.findAllUsers().size());
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

        log.debug("Sevice: Creating user : {}.", user.toString());
        return users.addNewUser(user);
    }

    /**
     * Метод чтения информации о пользователе по заданному идентификатору
     *
     * @param id - идентификатор пользователя
     * @return - найденный объект
     */
    public User getUserById(Integer id) {
        log.debug("Sevice: Get user {}.", id);
        return users.getUserById(id);
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
        User user = new User(users.getUserById(id));

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

        log.debug("Sevice: Updating user id={} : {}", id, user.toString());
        return users.updateUser(user);
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
        // Добавление в друзья происходит без подтверждения.
        // Еслb id1 дружит с id2, то автоматически id2 дружит с id1
        users.addFriend(id1, id2);
        users.addFriend(id2, id1);
    }

    /**
     * Метод удаления пользователя из "друзей"
     *
     * @param id1 - идентификатор пользователя
     * @param id2 - идентификатор друга
     * @return - сообщение о подтверждении
     */
    public void breakUpFriends(Integer id1, Integer id2) {
        log.debug("Sevice: Удаляем из \"друзей\" пользователей {}, {}.", id1, id2);
        users.breakUpFriends(id1, id2);
    }

    /**
     * Поиск всех друзей пользователя
     *
     * @param userId - идентификатор пользователя
     * @return - список друзей
     */
    public Collection<User> getUsersFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        for (Integer friendId : users.findAllFriends(userId)) {
            friends.add(users.getUserById(friendId));
        }
        return friends;
    }

    /**
     * Метод поискаобщих друзей пользователей
     *
     * @param id1 - идентификатор пользователя
     * @param id2 - идентификатор другого пользователя
     * @return - список общих друзей
     */
    public Collection<User> getCommonFriends(Integer id1, Integer id2) {
        List<Integer> friendsId = users.findAllFriends(id1);
        friendsId.retainAll(users.findAllFriends(id2));
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(users.getUserById(id));
        }
        return friends;
    }
}
