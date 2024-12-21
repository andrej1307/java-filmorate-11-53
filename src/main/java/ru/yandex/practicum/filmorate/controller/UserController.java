package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Класс обработки http запросов о пользователях.
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController extends AbstractController<User> {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * Метод поиска всех пользователей
     *
     * @return - список пользователей
     */
    @GetMapping
    public Collection<User> findAllUser() {
        log.info("Get all users {}.", super.findAll().size());
        return super.findAll();
    }

    /**
     * Метод добавления нового пользователя.
     *
     * @param user - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    @PostMapping
    public User addNewUser(@Validated(Marker.OnBasic.class) @RequestBody User user) {
        // "имя для отображения может быть пустым
        // — в таком случае будет использован логин" (ТЗ-№10)
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        log.info("Creating user : {}.", user.toString());
        return super.addNew(user);
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
    public User updateUser(@Validated(Marker.OnUpdate.class) @RequestBody User updUser) {
        Integer id = updUser.getId();
        User user = new User(super.getElement(id));

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

        log.info("Updating user id={} : {}", id, user.toString());
        return super.update(user);
    }

    /**
     * Удаление всех пользователей
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping
    public String onDelete() {
        log.info("Deleting all users.");
        clear();
        return "All users deleted.";
    }

}
