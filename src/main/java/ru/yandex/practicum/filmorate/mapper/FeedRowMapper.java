package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {

    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        long timestamp = resultSet.getLong("timestamp");
        feed.setTimestamp(timestamp);
        feed.setUserId(resultSet.getInt("user_id"));
        feed.setEventType(EventType.valueOf(resultSet.getString("event_type")));
        feed.setOperation(Operation.valueOf(resultSet.getString("operation_type")));
        feed.setEventId(resultSet.getInt("event_id"));
        feed.setEntityId(resultSet.getInt("entity_id"));

        return feed;
    }
}