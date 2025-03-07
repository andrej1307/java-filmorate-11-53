package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.LocalDateAdapter;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестируем контроллер запросов работы с данными о фильмах
 * <p>
 * Для успешного выполнения тестов, при инициализации базы данных
 * должна быть подготовлена информация о четырех тестовых фильмах и
 * о четырех тестовых пользователях.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private static final int TEST_FILM_ID = 1;

    @Autowired
    MockMvc mvc;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // Определяем тип сериализации списка
    class FilmListTypeToken extends TypeToken<List<Film>> {
    }

    /**
     * Генерация информации о тестовом фильме
     * Поля должны соответствовать содержимому базы данных для фильма TEST_FILM_ID
     *
     * @return - объект, который ожидается для TEST_FILM_ID
     */
    static Film getTestFilm() {
        Film film = new Film();
        film.setId(TEST_FILM_ID);
        film.setName("TestFilmName");
        film.setDescription("TestFilmDescription");
        film.setReleaseDate(LocalDate.of(2001, 2, 3));
        film.setDuration(51);
        film.setMpa(new Mpa(1));
        film.addGenre(new Genre(1, "Комедия"));
        return film;
    }

    /**
     * Тестируем поиск всех фильмов
     */
    @Test
    void findAllFilms() throws Exception {
        MvcResult result = mvc.perform(get("/films"))
                .andExpect(status().isOk())      // ожидается код статус 200
                .andReturn();
        List<Film> films = gson.fromJson(result.getResponse().getContentAsString(),
                new FilmListTypeToken().getType());
        assertTrue(!films.isEmpty(),
                "Список фильмов пуст.");
    }

    /**
     * Тестируем поиск фильма по идентификатору
     */
    @Test
    void findFilm() throws Exception {
        // попытка поиска несуществующего фильма
        mvc.perform(get("/films/10000"))
                .andExpect(status().isNotFound());      // ожидается код статус 404

        // поиск тестового фильма
        MvcResult result = mvc.perform(get("/films/" + TEST_FILM_ID))
                .andExpect(status().isOk())      // ожидается код статус 200
                .andReturn();
        Film filmDb = gson.fromJson(result.getResponse().getContentAsString(), Film.class);
        assertThat(filmDb)
                .withFailMessage("Считанный объект не соответствует ожидаемому.")
                .isEqualTo(getTestFilm());
    }

    /**
     * Тестируем добавление информации о новом фильме.
     */
    @Test
    void addNewFilm() throws Exception {
        Film film = getTestFilm();
        film.setId(null);
        film.setName("NewTestFilm");
        String jsonString = gson.toJson(film);

        // При успешном добавлении фильма
        // должен возвращаться статус 201 "Created"
        MvcResult result = mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        Film filmDb = gson.fromJson(result.getResponse().getContentAsString(), Film.class);
        assertNotNull(filmDb.getId(),
                "при добавлении фильма должен присваиваться ненулевой идентификатор.");
    }

    /**
     * Тестируем обновление информации о фильме
     */
    @Test
    void updateFilm() throws Exception {
        Film film = getTestFilm();
        film.setId(null);
        film.setName("TestFilmForUpdate");
        String jsonString = gson.toJson(film);

        // При успешном добавлении фильма
        // должен возвращаться статус 201 "Created"
        MvcResult result = mvc.perform(post("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        Film filmDb = gson.fromJson(result.getResponse().getContentAsString(), Film.class);

        // Готовим информацию для обновления
        film.setName("TestFilmNameUpdated");
        film.setDescription("Description updated");
        film.addGenre(new Genre(3, "Мультфильм"));
        jsonString = gson.toJson(film);

        jsonString = gson.toJson(film);
        // При обновлении фильма с отсутствующим id
        // должен возвращаться статус 400 "BadRequest"
        mvc.perform(put("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        film.setId(1000);
        jsonString = gson.toJson(film);
        // При обновлении фильма с неверным id
        // должен возвращаться статус 404 "NotFound"
        mvc.perform(put("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        film.setId(filmDb.getId());
        jsonString = gson.toJson(film);
        // При обновлении фильма с корректным id
        // должен возвращаться статус 200 "Ok"
        result = mvc.perform(put("/films")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        filmDb = gson.fromJson(result.getResponse().getContentAsString(), Film.class);
        assertThat(filmDb)
                .withFailMessage("Обновленный объект не соответствует ожидаемому.")
                .isEqualTo(film);
    }

    /**
     * Тестируем добавление "лайка"
     *
     * @throws Exception
     */
    @Test
    void addLike() throws Exception {
        // При добавлении "лайка" от несуществующего пользователя
        // должен возвращаться статус 404 "NotFound"
        mvc.perform(put("/films/1/like/1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // При добавлении "лайка"
        // должен возвращаться статус 200 "Ok"
        mvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тестируем удаление "лайка"
     */
    @Test
    void removeLike() throws Exception {
        addLike();

        // При удалении "лайка"
        // должен возвращаться статус 200 "Ok"
        mvc.perform(delete("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findPopularFilms() throws Exception {
        mvc.perform(put("/films/1/like/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/2/like/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/2/like/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/3/like/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/3/like/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/3/like/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/3/like/4").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/4/like/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/4/like/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(put("/films/4/like/3").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        MvcResult result = mvc.perform(get("/films/popular?count=2"))
                .andExpect(status().isOk())      // ожидается код статус 200
                .andReturn();
        List<Film> filmsPopular = gson.fromJson(result.getResponse().getContentAsString(),
                new FilmListTypeToken().getType());
        assertTrue(filmsPopular.size() == 2,
                "Число популярных фильмов не соответствует ожидаемому");
    }

}