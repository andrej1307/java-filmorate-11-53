package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;


import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private static final String SQL_INSERT_REVIEW = "INSERT INTO reviews (content, is_positive, film_id, user_id, useful) " +
            "VALUES (:content, :is_positive, :film_id, :user_id, 0)";
    private static final String SQL_DELETE_REVIEW = "DELETE FROM reviews WHERE id = :id";
    private static final String SQL_UPDATE_REVIEW = "UPDATE reviews SET content = :content, " +
            "is_positive = :is_positive, film_id = :film_id, user_id = :user_id WHERE id = :id";
    private static final String SQL_SELECT_REVIEW_BY_ID = "SELECT * FROM reviews WHERE id = :id";
    private static final String SQL_SELECT_REVIEWS_BY_FILM_ID = "SELECT * FROM ( " +
            "SELECT *, ROW_NUMBER() OVER (PARTITION BY film_id ORDER BY useful DESC) as rn " +
            "FROM reviews WHERE film_id = IFNULL(:film_id, film_id) ) t " +
            "WHERE rn <= IFNULL(:count, 10)";
    private static final String SQL_INSERT_FEEDBACK = "INSERT INTO feedbacks (review_id, user_id, is_like) " +
            "VALUES (:review_id, :user_id, :is_like) ON CONFLICT (review_id, user_id) DO NOTHING";
    private static final String SQL_UPDATE_REVIEW_USEFUL_BY_REVIEW_ID = "UPDATE reviews SET useful = useful + :change " +
            "WHERE id = :id";
    private static final String SQL_DELETE_FEEDBACK = "DELETE FROM feedbacks " +
            "WHERE review_id = :review_id AND user_id = :user_id";


    @Autowired
    private final NamedParameterJdbcTemplate jdbc;

    private final ReviewRowMapper reviewRowMapper;

    /**
     * Добавляет новый отзыв в базу данных.
     *
     * @param review Объект Review, содержащий информацию о новом отзыве.
     * @return Объект Review, содержащий информацию о добавленном отзыве.
     * @throws InternalServerException Если произошла ошибка при добавлении отзыва.
     */
    @Override
    public Review addReview(Review review) {

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        try {
            jdbc.update(SQL_INSERT_REVIEW,
                    new MapSqlParameterSource()
                            .addValue("content", review.getContent())
                            .addValue("is_positive", review.isPositive())
                            .addValue("film_id", review.getFilmId())
                            .addValue("user_id", review.getUserId()),
                    generatedKeyHolder
            );
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при добавлении отзыва.");
        }

        return getReviewById(generatedKeyHolder.getKey().intValue()).get();
    }

    /**
     * Удаляет отзыв из базы данных по его идентификатору.
     *
     * @param id Идентификатор отзыва, который нужно удалить.
     * @throws InternalServerException Если произошла ошибка при удалении отзыва.
     */
    @Override
    public void deleteReview(Integer id) {
        try {
            jdbc.update(SQL_DELETE_REVIEW, new MapSqlParameterSource()
                    .addValue("id", id));
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при удалении отзыва.");
        }
    }

    /**
     * Обновляет существующий отзыв в базе данных.
     *
     * @param review Объект Review, содержащий информацию об обновленном отзыве.
     * @return Объект Optional, содержащий обновленный отзыв, если он был успешно обновлен,
     * или пустой Optional, если отзыв не найден.
     * @throws InternalServerException Если произошла ошибка при обновлении отзыва.
     */
    @Override
    public Review updateReview(Review review) {
        try {
            jdbc.update(SQL_UPDATE_REVIEW,
                    new MapSqlParameterSource()
                            .addValue("content", review.getContent())
                            .addValue("is_positive", review.isPositive())
                            .addValue("film_id", review.getFilmId())
                            .addValue("user_id", review.getUserId())
                            .addValue("id", review.getId()));
            return getReviewById(review.getId()).get();
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при обновлении отзыва.");
        }
    }

    /**
     * Возвращает отзыв из базы данных по его идентификатору.
     *
     * @param id Идентификатор отзыва, который нужно получить.
     * @return Объект Optional, содержащий отзыв, если он найден, или пустой Optional, если отзыв не найден.
     * @throws InternalServerException Если произошла ошибка при получении отзыва.
     */
    @Override
    public Optional<Review> getReviewById(Integer id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL_SELECT_REVIEW_BY_ID,
                    new MapSqlParameterSource().addValue("id", id), reviewRowMapper));
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при получении отзыва.");
        }
    }

    /**
     * Возвращает список отзывов из базы данных для заданного фильма.
     *
     * @param filmId Идентификатор фильма, для которого нужно получить список отзывов.
     * @param count  Количество отзывов, которое нужно получить.
     * @return Список объектов Review, содержащих информацию об отзывах.
     */
    @Override
    public Collection<Review> getReviews(Integer filmId, Integer count) {
        try {
            return jdbc.query(SQL_SELECT_REVIEWS_BY_FILM_ID,
                    new MapSqlParameterSource()
                            .addValue("film_id", filmId)
                            .addValue("count", count), reviewRowMapper);
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при получении списка отзывов для фильма.");
        }
    }

    /**
     * Добавляет Like/Dislike к отзыву.
     *
     * @param reviewId Идентификатор отзыва, к которому нужно добавить Like/Dislike.
     * @param userId   Идентификатор пользователя, который добавляет Like/Dislike.
     * @param isLike   Значение true, если Like, false, если Dislike.
     */
    @Override
    public Review addFeedback(Integer reviewId, Integer userId, Boolean isLike) {
        try {
            jdbc.update(SQL_INSERT_FEEDBACK,
                    new MapSqlParameterSource()
                            .addValue("review_id", reviewId)
                            .addValue("user_id", userId)
                            .addValue("is_like", isLike));
            jdbc.update(SQL_UPDATE_REVIEW_USEFUL_BY_REVIEW_ID,
                    new MapSqlParameterSource()
                            .addValue("change", isLike ? 1 : -1)
                            .addValue("id", reviewId));
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при добавлении Like/Dislike для отзыва.");
        }
        return getReviewById(reviewId).get();
    }

    /**
     * Удаляет Like/Dislike из отзыва.
     *
     * @param reviewId Идентификатор отзыва, из которого нужно удалить Like/Dislike.
     * @param userId   Идентификатор пользователя, который удаляет Like/Dislike.
     */
    @Override
    public Review deleteFeedback(Integer reviewId, Integer userId) {
        try {
            boolean change = Boolean.TRUE.equals(jdbc.queryForObject(
                    "SELECT is_like FROM feedbacks WHERE review_id = :review_id AND user_id = :user_id",
                    new MapSqlParameterSource()
                            .addValue("review_id", reviewId)
                            .addValue("user_id", userId),
                    Boolean.class));
            jdbc.update(SQL_DELETE_FEEDBACK,
                    new MapSqlParameterSource()
                            .addValue("review_id", reviewId)
                            .addValue("user_id", userId));
            jdbc.update(SQL_UPDATE_REVIEW_USEFUL_BY_REVIEW_ID,
                    new MapSqlParameterSource()
                            .addValue("change", change ? -1 : 1)
                            .addValue("id", reviewId));
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при удалении Like/Dislike для отзыва.");
        }
        return getReviewById(reviewId).get();
    }

    /**
     * Проверяет, существует ли Like/Dislike с заданным идентификатором отзыва и пользователем.
     *
     * @param reviewId Идентификатор отзыва, который нужно проверить.
     * @param userId   Идентификатор пользователя, который нужно проверить.
     * @return true, если Like/Dislike существует, false в противном случае.
     */
    @Override
    public boolean containsFeedback(Integer reviewId, Integer userId) {
        try {
            return jdbc.queryForObject("SELECT review_id FROM feedbacks WHERE review_id = :review_id AND user_id = :user_id",
                    new MapSqlParameterSource()
                            .addValue("review_id", reviewId)
                            .addValue("user_id", userId), Integer.class) != null;
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при работе с базой данных.");
        }
    }
}
