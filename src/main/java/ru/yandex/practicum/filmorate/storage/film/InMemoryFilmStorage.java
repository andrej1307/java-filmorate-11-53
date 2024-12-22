package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryAbstractStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class InMemoryFilmStorage extends InMemoryAbstractStorage<Film> implements FilmStorage {

    private final Map<Integer, HashSet<Integer>> likes = new HashMap<>();

    @Override
    public Film addNewFilm(Film newFilm) {
        Film film = super.addNew(newFilm);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        return super.getElement(id);
    }

    @Override
    public Collection<Film> findAllFilms() {
        return super.findAll();
    }

    @Override
    public Film updateFilm(Film updFilm) {
        return super.update(updFilm);
    }

    /**
     * Добавление "лайка" к фильму.
     *
     * @param filmId - идентифмкатор фильма
     * @param userId - идентификатор пользователя
     * @return - число никальных лайков
     */
    @Override
    public Integer addNewLike(Integer filmId, Integer userId) {
        if (likes.containsKey(filmId)) {
            likes.get(filmId).add(userId);
        } else {
            throw new NotFoundException("Не найден фильм id=" + filmId);
        }
        return likes.get(filmId).size();
    }

    /**
     * Удаление "лайка" у фильма
     *
     * @param filmId - идентификатор фильма
     * @param userId - идентификатор пользователя
     * @return - число независимых "лайков" у фильма
     */
    @Override
    public Integer removeLike(Integer filmId, Integer userId) {
        if (likes.containsKey(filmId)) {
            if (!likes.get(filmId).remove(userId)) {
                throw new NotFoundException("Не найден \"лайк\" id=" + userId);
            }
        } else {
            throw new NotFoundException("Не найден фильм id=" + filmId);
        }
        return likes.get(filmId).size();
    }

    @Override
    public void removeAllFilms() {
        likes.clear();
        super.clear();
    }
}
