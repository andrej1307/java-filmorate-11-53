package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> likes = new HashMap<>();
    private final List<Film> filmsRating = new ArrayList<>();
    Integer filmId = 0;

    @Override
    public Film addNewFilm(Film film) {
        filmId++;
        film.setId(filmId);
        // film.setRank(0);
        films.put(filmId, film);
        likes.put(filmId, new HashSet<>());
        filmsRating.add(film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public void updateFilm(Film updFilm) {
        films.put(updFilm.getId(), updFilm);
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
        likes.get(filmId).add(userId);
        Film film = films.get(filmId);
        // film.setRank(likes.get(filmId).size());
        setFilmsRating(film);
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
        likes.get(filmId).remove(userId);
        Film film = films.get(filmId);
        // film.setRank(likes.get(filmId).size());
        setFilmsRating(film);
        return likes.get(filmId).size();
    }

    @Override
    public Integer getFilmRank(Integer filmId) {
        int filmRank = 0;
        if (likes.containsKey(filmId)) {
            filmRank = likes.get(filmId).size();
        }
        return filmRank;
    }

    /**
     * Определение позиции фильма в рейтинге.
     * Так как рейтинг представляет собой уже упорядоченный список,
     * то сортировать весь список нет смысла.
     * Нужно уточнить место в рейтинге заданного объекта.
     *
     * @param film
     */
    private void setFilmsRating(Film film) {
        int ratingSize = filmsRating.size();

        // Если фильмов меньше двух, то ничего не делаем
        if (ratingSize < 2) {
            return;
        }

        int index = filmsRating.indexOf(film);

        Integer filmRank = getFilmRank(film.getId());

        // Проверяем изменение рейтинга на возрастание
        while ((index > 0) &&
                (filmRank > likes.get(filmsRating.get(index - 1).getId()).size())) {
            filmsRating.set(index, filmsRating.get(index - 1));
            filmsRating.set(--index, film);
        }

        // Проверяем изменение рейтинга на убывание
        while (index < (ratingSize - 1) &&
                (filmRank < likes.get(filmsRating.get(index + 1).getId()).size())) {
            filmsRating.set(index, filmsRating.get(index + 1));
            filmsRating.set(++index, film);
        }
    }

    /**
     * Поиск самых популярных фильмов
     *
     * @param count - количество фильмов для поиска
     * @return - список фильмов
     */
    @Override
    public Collection<Film> findPopularFilms(int count) {
        if (count > filmsRating.size()) {
            count = filmsRating.size();
        }
        return filmsRating.subList(0, count);

    }

    @Override
    public void removeAllFilms() {
        likes.clear();
        filmsRating.clear();
        films.clear();
        filmId = 0;
    }
}
