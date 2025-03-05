package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review addReview(Review review);

    void deleteReview(Integer id);

    Review updateReview(Review review);

    Optional<Review> getReviewById(Integer id);

    Collection<Review> getReviews(Integer filmId, Integer count);

    Review addFeedback(Integer reviewId, Integer userId, Boolean isLike);

    Review deleteFeedback(Integer reviewId, Integer userId);

    boolean containsFeedback(Integer reviewId, Integer userId);
}