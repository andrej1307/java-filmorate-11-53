package ru.yandex.practicum.filmorate.storage.dbadmin;

public interface AdminStorage {


    void removeAllFilms();

    void removeAllUsers();

    void removeFilmsById(Integer id);

    void removeUsersById(Integer id);


}
