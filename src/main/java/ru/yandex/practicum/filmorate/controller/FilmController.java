package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import java.util.Collection;

/**
 * Класс обработки http запросов к фильмам.
 */
@RestController
@RequestMapping("/films")
public class FilmController extends AbstractController<Film> {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    /**
     * Метод поиска всех фильмов
     *
     * @return - список фильмов
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Get all films {}.", super.findAll().size());
        return super.findAll();
    }

    /**
     * Метод добавления нового фильма.
     *
     * @param film - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    @PostMapping
    public Film addNewFilm(@Validated(Marker.OnBasic.class) @RequestBody Film film) {
        log.info("Creating film: {}.", film.toString());
        return super.addNew(film);
    }


    /**
     * Метод обновления информации о фильме.
     * При вызове метода промзводится проверка аннотаций только для маркера OnUpdate.class.
     * Кроме id любой другой параметр может отсутствовать
     *
     * @param updFilm - объект с обновленной информацией о фильме
     * @return - подтверждение обновленного объекта
     */
    @PutMapping
    public Film updateFilm(@Validated(Marker.OnUpdate.class) @RequestBody Film updFilm) {
        Integer id = updFilm.getId();
        Film film = new Film(getElement(id));

        // Обновляем информаию во временном объекте
        if (updFilm.getName() != null) {
            film.setName(updFilm.getName());
        }
        if (updFilm.getDescription() != null) {
            film.setDescription(updFilm.getDescription());
        }
        if (updFilm.getReleaseDate() != null) {
            film.setReleaseDate(updFilm.getReleaseDate());
        }
        if (updFilm.getDuration() > 0) {
            film.setDuration(updFilm.getDuration());
        }

        log.info("Updating film id={} : {}", id, film.toString());
        return super.update(film);
    }

    /**
     * Удаление всех фильмов
     *
     * @return - сообщение о выполнении
     */
    @DeleteMapping
    public String onDelete() {
        log.info("Deleting all films.");
        clear();
        return "All films deleted.";
    }

}
