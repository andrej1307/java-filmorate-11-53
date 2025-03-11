package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

public interface FeedStorage {

    Collection<Feed> findAllByUserId(Integer id);

    void create(Feed feed);
}