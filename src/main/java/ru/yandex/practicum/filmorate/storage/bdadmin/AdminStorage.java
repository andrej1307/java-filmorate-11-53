package ru.yandex.practicum.filmorate.storage.bdadmin;

public interface AdminStorage {


    void removeAllFilms();

    void removeAllUsers();

    void removeFilmsById(Integer id);

    void removeUsersById(Integer id);


}
