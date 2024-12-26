package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

/**
 * Класс обработки http запросов о пользователях.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Метод поиска всех пользователей
     *
     * @return - список пользователей
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAllUser() {
        log.info("Запрашиваем список всех пользователей {}.", service.findAllUsers().size());
        return service.findAllUsers();
    }

    /**
     * Метод поиска пользователя по идентификатору
     *
     * @param id - идентификатор
     * @return - найденный объект
     */
    @GetMapping("/{id}")
    public User findUser(@PathVariable Integer id) {
        log.info("Ищем пользователя id={}.", id);
        return service.getUserById(id);
    }

    /**
     * Поиск друзей у заданного пользователя
     *
     * @param id - идентификатор пользователя
     * @return - список друзей пользователя
     */
    @GetMapping("/{id}/friends")
    public Collection<User> findUsersFriends(@PathVariable Integer id) {
        log.info("Ищем друзей пользователя id={}.", id);
        return service.getUsersFriends(id);
    }

    /**
     * Метод поиска общих друзей у двух пользователей
     *
     * @param id      - идентификатор пользователя
     * @param otherId - идентификатор другого пользователя
     * @return - список общих друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") Integer id,
                                              @PathVariable("otherId") Integer otherId) {
        log.info("Ищем общих друзей пользователй: {}, {}.", id, otherId);
        return service.getCommonFriends(id, otherId);
    }

    /**
     * Метод добавления нового пользователя.
     *
     * @param user - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@Validated(Marker.OnBasic.class) @RequestBody User user) {
        log.info("Создаем пользователя : {}.", user.toString());
        return service.addNewUser(user);
    }

    /**
     * Метод обновления информации о пользователе.
     * При вызове метода промзводится проверка аннотаций только для маркера OnUpdate.class.
     * Кроме id любой другой параметр может отсутствовать
     *
     * @param updUser - объект с обновленной информацией о пользователе
     * @return - подтверждение обновленного объекта
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Validated(Marker.OnUpdate.class) @RequestBody User updUser) {
        Integer id = updUser.getId();
        log.info("Обновляем данные о пользователе id={} : {}", id, updUser.toString());
        return service.updateUser(updUser);
    }

    /**
     * Метод добаления в "друзья"
     *
     * @param userId   - идентификатор пользоателя
     * @param friendId - идентификатор друга
     * @return - сообщение о добавлении друга
     */
    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriends(@PathVariable("userId") Integer userId,
                           @PathVariable("friendId") Integer friendId) {
        log.info("Добавляем в \"друзья\" пользователей id1={}, id2={}", userId, friendId);
        service.addFriends(userId, friendId);
    }

    /**
     * Метод удаления пользователя из "друзей"
     *
     * @param id       - идентификатор пользователя
     * @param friendId - идентификатор друга
     * @return - сообщение о подтверждении
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void breakUpFriends(@PathVariable("id") Integer id,
                               @PathVariable("friendId") Integer friendId) {
        log.info("Удаляем из \"друзей\" пользователей id1={}, id2={}", id, friendId);
        service.breakUpFriends(id, friendId);
    }

    /**
     * Удаление всех пользователей
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String deleteAllUsers() {
        log.info("Удаляем всех пользователей.");
        return service.removeAllUsers();
    }

}
