package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationsDbStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class RecommendationsServiceImpl implements RecommendationsService {

    private final RecommendationsDbStorage recommendationsDbStorage;


    @Override
    public Collection<Film> getFilmsRecommendationsByUserId(int userId) {

        return recommendationsDbStorage.getFilmsRecommendationsByUserId(userId);
    }
}
