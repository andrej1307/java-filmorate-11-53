package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director findById(int id);

    void save(Director director);

    void update(Director director);

    void delete(int id);

    //======= добавляем новые методы

    // сохраняет привязку директоров к фильму
    void saveFilmDirectors(Film film);

    // ищет директорв привязанных к идентификатору фильма
    Collection<Director> findDirectorsByFilmId(Integer filmId);

    // ищет все определенные пары {filmId, Director}
    Collection<FilmDirector> findAllFilmDirector();

    // Ищет идентификаторы фильмов по подстроке в имени директора.
    // Вот хочется мне реализовать поиск напрямую в FilmDbtorage,
    // но правильнее будет использовать вызов метода в профильном хранилище.
    Collection<Integer> findDirectorsByName(String nameSubstring);
}
