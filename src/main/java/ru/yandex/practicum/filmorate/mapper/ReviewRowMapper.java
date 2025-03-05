package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setId(resultSet.getInt("id"));
        review.setContent(resultSet.getString("content"));
        review.setPositive(resultSet.getBoolean("is_positive"));
        review.setFilmId(resultSet.getInt("film_id"));
        review.setUserId(resultSet.getInt("user_id"));
        review.setUseful(resultSet.getInt("useful"));
        return review;
    }
}
