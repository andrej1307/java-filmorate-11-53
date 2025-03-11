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
    private static final String SQL_DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = :review_id";
    private static final String SQL_UPDATE_REVIEW = "UPDATE reviews SET content = :content, " +
            "is_positive = :is_positive, film_id = :film_id, user_id = :user_id WHERE review_id = :review_id";
    private static final String SQL_SELECT_REVIEW_BY_ID = "SELECT * FROM reviews WHERE review_id = :review_id";
    private static final String SQL_SELECT_REVIEWS_BY_FILM_ID = "SELECT * FROM ( " +
            "SELECT *, ROW_NUMBER() OVER (PARTITION BY film_id ORDER BY useful DESC) as rn " +
            "FROM reviews WHERE film_id = IFNULL(:film_id, film_id) ) t " +
            "WHERE rn <= :count";
    private static final String SQL_DELETE_FEEDBACK = "DELETE FROM feedbacks " +
            "WHERE reviewid_id = :reviewid_id AND user_id = :user_id";
    private static final String SQL_INSERT_REVIEW_LIKE = "INSERT INTO feedbacks (reviewid_id, user_id, is_like) " +
            "VALUES (:reviewid_id, :user_id, :is_like)";
    private static final String SQL_UPDATE_RATING_REVIEW_LIKE = "UPDATE reviews SET useful=useful+1 "+
            "WHERE review_id = :review_id";
    private static final String SQL_UPDATE_FEEDBACK_DISLIKE_LIKE = "UPDATE feedbacks SET is_like=:is_like "+
            "WHERE reviewid_id=:reviewid_id AND user_id=:user_id";
    private static final String SQL_UPDATE_REVIEW_DISLIKE_LIKE = "UPDATE reviews SET useful=useful+2 "+
            "WHERE review_id = :review_id";
    private static final String SQL_INSERT_REVIEW_DISLIKE = "INSERT INTO feedbacks (reviewid_id, user_id, is_like) " +
            "VALUES (:reviewid_id, :user_id, :is_like)";
    private static final String SQL_UPDATE_RATING_REVIEW_DISLIKE = "UPDATE reviews SET useful=useful-1 "+
            "WHERE review_id = :review_id";
    private static final String SQL_UPDATE_FEEDBACK_LIKE_DISLIKE = "UPDATE feedbacks SET is_like=:is_like WHERE "+
            "reviewid_id=:reviewid_id AND user_id=:user_id";
    private static final String SQL_UPDATE_REVIEW_LIKE_DISLIKE = "UPDATE reviews SET useful=useful-2 WHERE "+
            "review_id = :review_id";
    private static final String SQL_SELECT_FEEDBACK_BY_REVIEWID_ID_AND_USER_ID = "SELECT is_like FROM feedbacks "+
            "WHERE reviewid_id = :reviewid_id AND user_id = :user_id";
    private static final String SQL_DELETE_FEEDBACK_DISLIKE = "UPDATE reviews SET useful=useful+1 "+
            "WHERE review_id = :review_id";
    private static final String SQL_DELETE_FEEDBACK_LIKE = "UPDATE reviews SET useful=useful-1 "+
            "WHERE review_id = :review_id";
    private static final String SQL_SELECT_CHECK_FEEDBACK = "SELECT reviewid_id FROM feedbacks "+
            "WHERE reviewid_id = :reviewid_id AND user_id = :user_id";


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
                            .addValue("is_positive", review.getIsPositive())
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
     * @param reviewId Идентификатор отзыва, который нужно удалить.
     * @throws InternalServerException Если произошла ошибка при удалении отзыва.
     */
    @Override
    public void deleteReview(Integer reviewId) {
        try {
            jdbc.update(SQL_DELETE_REVIEW, new MapSqlParameterSource()
                    .addValue("review_id", reviewId));
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
                            .addValue("review_id", review.getReviewId())
                            .addValue("content", review.getContent())
                            .addValue("is_positive", review.getIsPositive())
                            .addValue("film_id", review.getFilmId())
                            .addValue("user_id", review.getUserId())
                            .addValue("useful", review.getUseful()));
            return getReviewById(review.getReviewId()).get();
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при обновлении отзыва.");
        }
    }

    /**
     * Возвращает отзыв из базы данных по его идентификатору.
     *
     * @param reviewId Идентификатор отзыва, который нужно получить.
     * @return Объект Optional, содержащий отзыв, если он найден, или пустой Optional, если отзыв не найден.
     * @throws InternalServerException Если произошла ошибка при получении отзыва.
     */
    @Override
    public Optional<Review> getReviewById(Integer reviewId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL_SELECT_REVIEW_BY_ID,
                    new MapSqlParameterSource()
                            .addValue("review_id", reviewId), reviewRowMapper));
        } catch (DataAccessException ignored) {
            return Optional.empty();
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
     * Добавляет Like к отзыву.
     *
     * @param reviewIdId Идентификатор отзыва, к которому нужно добавить Like.
     * @param userId     Идентификатор пользователя, который добавляет Like.
     */
    @Override
    public Review addLike(Integer reviewIdId, Integer userId) {
        try {
            if (!containsFeedback(reviewIdId, userId)) {
                jdbc.update(SQL_INSERT_REVIEW_LIKE,
                        new MapSqlParameterSource()
                                .addValue("reviewid_id", reviewIdId)
                                .addValue("user_id", userId)
                                .addValue("is_like", true));
                jdbc.update(SQL_UPDATE_RATING_REVIEW_LIKE,
                        new MapSqlParameterSource()
                                .addValue("review_id", reviewIdId));
            } else {
                jdbc.update(SQL_UPDATE_FEEDBACK_DISLIKE_LIKE,
                        new MapSqlParameterSource()
                                .addValue("reviewid_id", reviewIdId)
                                .addValue("user_id", userId)
                                .addValue("is_like", true));
                jdbc.update(SQL_UPDATE_REVIEW_DISLIKE_LIKE,
                        new MapSqlParameterSource()
                                .addValue("review_id", reviewIdId));
            }
        } catch (
                DataAccessException e) {
            throw new InternalServerException("Ошибка при добавлении Like для отзыва.");
        }
        return getReviewById(reviewIdId).get();
    }

    /**
     * Добавляет Dislike к отзыву.
     *
     * @param reviewIdId Идентификатор отзыва, к которому нужно добавить Dislike.
     * @param userId     Идентификатор пользователя, который добавляет Dislike.
     */
    @Override
    public Review addDisLike(Integer reviewIdId, Integer userId) {
        try {
            if (!containsFeedback(reviewIdId, userId)) {
                jdbc.update(SQL_INSERT_REVIEW_DISLIKE,
                        new MapSqlParameterSource()
                                .addValue("reviewid_id", reviewIdId)
                                .addValue("user_id", userId)
                                .addValue("is_like", false));
                jdbc.update(SQL_UPDATE_RATING_REVIEW_DISLIKE,
                        new MapSqlParameterSource()
                                .addValue("review_id", reviewIdId));
            } else {
                jdbc.update(SQL_UPDATE_FEEDBACK_LIKE_DISLIKE,
                        new MapSqlParameterSource()
                                .addValue("reviewid_id", reviewIdId)
                                .addValue("user_id", userId)
                                .addValue("is_like", false));
                jdbc.update(SQL_UPDATE_REVIEW_LIKE_DISLIKE,
                        new MapSqlParameterSource()
                                .addValue("review_id", reviewIdId));
            }
        } catch (
                DataAccessException e) {
            throw new InternalServerException("Ошибка при добавлении Like для отзыва.");
        }
        return getReviewById(reviewIdId).get();
    }

    /**
     * Удаляет Like/Dislike из отзыва.
     *
     * @param reviewIdId Идентификатор отзыва, из которого нужно удалить Like/Dislike.
     * @param userId     Идентификатор пользователя, который удаляет Like/Dislike.
     */
    @Override
    public Review deleteFeedback(Integer reviewIdId, Integer userId) {
        try {


            boolean change = Boolean.TRUE.equals(jdbc.queryForObject(SQL_SELECT_FEEDBACK_BY_REVIEWID_ID_AND_USER_ID,
                    new MapSqlParameterSource()
                            .addValue("reviewid_id", reviewIdId)
                            .addValue("user_id", userId),
                    Boolean.class));
            jdbc.update(SQL_DELETE_FEEDBACK,
                    new MapSqlParameterSource()
                            .addValue("reviewid_id", reviewIdId)
                            .addValue("user_id", userId));
            if (!change) {
                jdbc.update(SQL_DELETE_FEEDBACK_DISLIKE,
                        new MapSqlParameterSource()
                                .addValue("review_id", reviewIdId));
            } else {
                jdbc.update(SQL_DELETE_FEEDBACK_LIKE,
                        new MapSqlParameterSource()
                                .addValue("review_id", reviewIdId));
            }
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при удалении Like/Dislike для отзыва.");
        }
        return getReviewById(reviewIdId).get();
    }

    /**
     * Проверяет, существует ли Like/Dislike с заданным идентификатором отзыва и пользователем.
     *
     * @param reviewIdId Идентификатор отзыва, который нужно проверить.
     * @param userId     Идентификатор пользователя, который нужно проверить.
     * @return true, если Like/Dislike существует, false в противном случае.
     */
    @Override
    public boolean containsFeedback(Integer reviewIdId, Integer userId) {
        try {
            return !jdbc.queryForList(SQL_SELECT_CHECK_FEEDBACK,
                    new MapSqlParameterSource()
                            .addValue("reviewid_id", reviewIdId)
                            .addValue("user_id", userId), Integer.class).isEmpty();
        } catch (DataAccessException ignored) {
            throw new InternalServerException("Ошибка при работе с базой данных.");
        }
    }
}
