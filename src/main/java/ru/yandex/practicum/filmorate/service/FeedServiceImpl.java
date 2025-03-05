package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class FeedServiceImpl implements FeedService {

    private final UserStorage users;
    private final FeedStorage feeds;

    public FeedServiceImpl(UserStorage users, FeedStorage feeds) {
        this.users = users;
        this.feeds = feeds;
    }

    @Override
    public Collection<Feed> findAllFeeds(Integer userId) {
        users.getUserById(userId);
        return feeds.findAllByUserId(userId);
    }

    public void createFeed(Integer userId, EventType event, Operation operation, Integer entityId) {
        Feed feed = new Feed();

        feed.setTimestamp(System.currentTimeMillis());
        feed.setUserId(userId);
        feed.setEventType(event);
        feed.setOperation(operation);
        feed.setEntityId(entityId);

        feeds.create(feed);
    }
}
