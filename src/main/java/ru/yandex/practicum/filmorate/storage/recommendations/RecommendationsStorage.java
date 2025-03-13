package ru.yandex.practicum.filmorate.storage.recommendations;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface RecommendationsStorage {

    // чтение фильма по списку идентификаторов
    Collection<Film> getFilmsRecommendationsByUserId(Integer userId);


}
