package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review addReview(Review reviewId);

    void deleteReview(Integer reviewId);

    Review updateReview(Review review);

    Optional<Review> getReviewById(Integer reviewId);

    Collection<Review> getReviews(Integer filmId, Integer count);

    Review addLike(Integer reviewIdId, Integer userId);

    Review addDisLike(Integer reviewIdId, Integer userId);

    Review deleteFeedback(Integer reviewIdId, Integer userId);

    boolean containsFeedback(Integer reviewIdId, Integer userId);
}