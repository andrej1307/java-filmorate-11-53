package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Review addReview(Review review);

    String deleteReview(Integer id);

    Review updateReview(Review review);

    Review getReviewById(Integer id);

    Collection<Review> getReviews(Integer filmId, Integer count);

    Review addFeedback(Integer reviewId, Integer userId, Boolean isLike);

    Review deleteFeedback(Integer reviewId, Integer userId);

}
