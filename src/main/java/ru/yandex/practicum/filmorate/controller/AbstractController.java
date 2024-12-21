package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.StorageData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс работы с элементами хранилища информации
 *
 * @param <T>
 */
public class AbstractController<T extends StorageData> {
    private final Map<Integer, T> storage = new HashMap<>();

    /**
     * Метод чтения элемента из хранилища
     *
     * @param id - идентификатор элемента
     * @return - объект
     */
    public T getElement(final Integer id) {
        T element = storage.get(id);
        if (element == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Не найден Id=" + id);
        }
        return storage.get(id);
    }

    /**
     * Метод поиска всех элементов
     *
     * @return - список элементов
     */
    public Collection<T> findAll() {
        return storage.values();
    }

    /**
     * Метод добавления нового элемента
     *
     * @param element - объект для добавления
     * @return - подтверждение добавленного объекта
     */
    public T addNew(@Validated @RequestBody T element) throws ValidationException {
        // Проверяем существование полльзователя для исключения дублирования
        if (storage.containsValue(element)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Уже существует : "
                    + element.toString());
        }

        element.setId(getNextId());
        storage.put(element.getId(), element);
        return element;
    }

    /**
     * Метод обновления элемента.
     *
     * @param element - объект с обновленной информацией
     * @return - подтверждение обновленного объекта
     */
    public T update(@Validated(Marker.OnUpdate.class) @RequestBody T element) throws ValidationException {
        Integer id = element.getId();
        // проверяем необходимые условия
        if (!storage.containsKey(id)) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Не найден Id=" + id);
        }
        storage.put(id, element);
        return element;
    }

    /**
     * Вспомогательный метод для генерации идентификатора нового элемента
     *
     * @return - актуальный идентификатор
     */
    private Integer getNextId() {
        Integer currentMaxId = storage.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    /**
     * Метод очистки хранилища
     */
    public void clear() {
        storage.clear();
    }
}
