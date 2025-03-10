package ru.yandex.practicum.filmorate.service;

public interface DbAdminService {

    String removeAllUsers();

    String removeAllFilms();

    String removeUsersById(Integer id);

    String removeFilmsById(Integer id);
}
