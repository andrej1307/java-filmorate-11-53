package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.Collection;

public interface FeedService {

    Collection<Feed> findAllFeeds(Integer userId);

    void createFeed(Integer userId, EventType event, Operation operation, Integer entityId);
}