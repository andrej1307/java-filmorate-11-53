package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
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

    /**
     * Возвращает ленту событий пользователя с указанным идентификатором.
     *
     * @param userId идентификатор пользователя, ленты которого нужно вернуть
     * @return список лент пользователя с указанным идентификатором
     */
    @Override
    public Collection<Feed> findAllFeeds(Integer userId) {
        User user = users.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Не найден пользователь id=" + userId));

        return feeds.findAllByUserId(userId);
    }

    /**
     * Создает запись в ленте событий.
     *
     * @param userId    идентификатор пользователя, совершившего событие
     * @param event     тип события
     * @param operation операция, связанная с событием
     * @param entityId  идентификатор сущности, с которой связано событие
     */
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
