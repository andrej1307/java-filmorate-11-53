package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.bdadmin.AdminDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

/**
 * Класс реализации запросов к информации о пользователях
 */
@Slf4j
@Service
@AllArgsConstructor
public class DbAdminServiceImpl implements DbAdminService {

    private final AdminDbStorage adminDbStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    /**
     * Удаление всех пользователей
     *
     * @return - сообщение о выполнении
     */
    @Override
    public String removeAllUsers() {
        log.debug("Sevice: Удаляем всех пользователей.");
        adminDbStorage.removeAllUsers();
        return "Все пользователи удалены.";
    }

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    @Override
    public String removeAllFilms() {
        log.debug("Sevice: Удаляем все фильмы.");
        adminDbStorage.removeAllFilms();
        return "Все фильмы удалены.";
    }


    /**
     * Удаление пользователя  по ID
     *
     * @return - сообщение о выполнении
     */
    @Override
    public String removeUsersById(Integer id) {
        log.debug("Sevice: Удаляем пользователя по ID");
        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        adminDbStorage.removeUsersById(id);
        return "Пользователь " + id + " удален.";
    }


//

    /**
     * Удаление фильма по ID
     *
     * @return - сообщение о выполнении
     */
    @Override
    public String removeFilmsById(Integer id) {
        log.debug("Sevice: Удаляем фильм по ID");
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Не найден фильм id=" + id));
        adminDbStorage.removeFilmsById(id);
        return "Фильм " + id + " удален.";
    }


}
