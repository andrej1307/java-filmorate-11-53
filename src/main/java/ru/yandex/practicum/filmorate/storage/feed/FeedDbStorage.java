package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private static final String SELECT_FEEDS_QUERY = "SELECT * FROM feed WHERE user_id = :user_id ORDER BY timestamp";
    private static final String INSERT_FEED_QUERY = "INSERT INTO feed (timestamp, user_id, event_type, operation_type, " +
            "entity_id) VALUES (:timestamp, :user_id, :event_type, :operation_type, :entity_id)";

    private final NamedParameterJdbcTemplate jdbc;

    /**
     * Создает новую запись в ленту.
     *
     * @param feed событие, которое нужно добавить в таблицу
     * @throws ValidationException если возникает ошибка при сохранении ленты
     */
    @Override
    public void create(Feed feed) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("timestamp", feed.getTimestamp())
                .addValue("user_id", feed.getUserId())
                .addValue("event_type", feed.getEventType().toString())
                .addValue("operation_type", feed.getOperation().toString())
                .addValue("entity_id", feed.getEntityId());

        jdbc.update(INSERT_FEED_QUERY, parameters, keyHolder, new String[]{"event_id"});

        if (keyHolder.getKey() != null) {
            feed.setEventId(keyHolder.getKey().intValue());
        } else {
            throw new ValidationException("Ошибка при сохранении события: не удалось сформировать ID");
        }
    }

    /**
     * Возвращает все записи из ленты событий для пользователя с указанным ID.
     *
     * @param userId ID пользователя
     * @return список всех записей в ленте для указанного пользователя
     */
    @Override
    public Collection<Feed> findAllByUserId(Integer userId) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("user_id", userId);

            return jdbc.query(SELECT_FEEDS_QUERY, parameters, new FeedRowMapper());
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }
}