package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.StorageData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс работы с хранилищем в оперативной памяти
 *
 * @param <T> - класс описания элементов хранилища
 */
public class InMemoryAbstractStorage<T extends StorageData> {
    private final Map<Integer, T> storage = new HashMap<>();

    /**
     * Метод чтения элемента из хранилища
     *
     * @param id - идентификатор элемента
     * @return - объект
     */
    public T getElement(final Integer id) throws NotFoundException {
        T element = storage.get(id);
        if (element == null) {
            throw new NotFoundException("Не найден Id=" + id);
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
    public T addNew(T element) throws ValidationException {
        // Проверяем существование полльзователя для исключения дублирования
        if (storage.containsValue(element)) {
            throw new ValidationException("Уже существует : " + element.toString());
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
    public T update(T element)
            throws NotFoundException {

        Integer id = element.getId();
        // проверяем необходимые условия
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Не найден Id=" + id);
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
