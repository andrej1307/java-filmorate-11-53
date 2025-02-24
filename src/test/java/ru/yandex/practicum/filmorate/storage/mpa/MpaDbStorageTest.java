package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование справочника рейтингов фильа
 * <p>
 * Для успешного выполнения тестов, при инициализации базы данных
 * должндолжен быть полностью заполнен справочник рейтингов MPA.
 * Файл первоначальных данных ./src/test/resources/data.sql
 */
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class})
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;
    private static List<Mpa> testMpaList = new ArrayList<>();

    /**
     * Инициализация эталонного списка рейтингов.
     */
    @BeforeAll
    static void setUp() {
        testMpaList.add(new Mpa(1, "G", "у фильма нет возрастных ограничений"));
        testMpaList.add(new Mpa(2, "PG", "детям рекомендуется смотреть фильм с родителями"));
        testMpaList.add(new Mpa(3, "PG-13", "детям до 13 лет просмотр не желателен"));
        testMpaList.add(new Mpa(4, "R", "лицам до 17 лет просматривать фильм можно только в присутствии взрослого"));
        testMpaList.add(new Mpa(5, "NC-17", "лицам до 18 лет просмотр запрещён"));
    }

    /**
     * Тестирование списка рейтингов
     */
    @Test
    void findAllMpa() {
        Collection<Mpa> genres = mpaDbStorage.findAllMpa();
        for (Mpa mpa : testMpaList) {
            assertTrue(genres.contains(mpa),
                    "В базе данных отсутствует " + mpa.toString());
        }
    }

    /**
     * Тестирование поиска рейтинга по идентификатору
     */
    @Test
    void findMpaById() {
        for (Mpa mpa : testMpaList) {
            Optional<Mpa> mpaOptional = mpaDbStorage.findMpa(mpa.getId());
            assertThat(mpaOptional)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(mpa);
        }
    }

}