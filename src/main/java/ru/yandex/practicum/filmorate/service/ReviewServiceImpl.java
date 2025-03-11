package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final FilmStorage films;
    private final UserStorage users;
    private final ReviewStorage reviews;
    private final FeedService feeds;

    /**
     * Добавляет новый отзыв в базу данных.
     *
     * @param review Объект Review, содержащий информацию о новом отзыве.
     * @return Объект Review, содержащий информацию о добавленном отзыве.
     * @throws NotFoundException Если фильм или пользователь с указанным id не найдены.
     */
    @Override
    public Review addReview(Review review) {
        if (films.getFilmById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Не найден фильм с id=" + review.getFilmId());
        }
        if (users.getUserById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id=" + review.getUserId());
        }
        Review result = reviews.addReview(review);
        feeds.createFeed(review.getUserId(), EventType.REVIEW, Operation.ADD,
                result.getReviewId());

        return result;
    }

    /**
     * Удаляет отзыв из базы данных.
     *
     * @param id id отзыва для удаления.
     * @return Сообщение об успешном удалении отзыва.
     * @throws NotFoundException Если отзыв с указанным id не найден.
     */
    @Override
    public void deleteReview(Integer id) {
        if (reviews.getReviewById(id).isEmpty()) {
            throw new NotFoundException("Не найден отзыв с id=" + id);
        }

        Review reviewDeleted = reviews.getReviewById(id).get();
        reviews.deleteReview(id);

        feeds.createFeed(reviewDeleted.getUserId(), EventType.REVIEW,
                Operation.REMOVE, reviewDeleted.getReviewId());

    }

    /**
     * Обновляет отзыв в базе данных.
     *
     * @param review Объект Review, содержащий информацию об обновленном отзыве.
     * @return Объект Review, содержащий информацию об обновленном отзыве.
     * @throws NotFoundException Если отзыв или фильм или пользователь с указанным id не найдены.
     */
    @Override
    public Review updateReview(Review review) {
        if (reviews.getReviewById(review.getReviewId()).isEmpty()) {
            throw new NotFoundException("Не найден отзыв с id=" + review.getReviewId());
        }
        if (films.getFilmById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Не найден фильм с id=" + review.getFilmId());
        }
        if (users.getUserById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id=" + review.getUserId());
        }
        Review result = reviews.updateReview(review);

        feeds.createFeed(result.getUserId(), EventType.REVIEW, Operation.UPDATE,
                result.getReviewId());

        return result;
    }

    /**
     * Возвращает отзыв по его id.
     *
     * @param id id отзыва для получения.
     * @return Объект Review, содержащий информацию об отзыве.
     * @throws NotFoundException Если отзыв с указанным id не найден.
     */
    @Override
    public Review getReviewById(Integer id) {
        return reviews.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Не найден отзыв с id=" + id));
    }

    /**
     * Возвращает список отзывов для фильма.
     *
     * @param filmId id фильма для получения отзывов, если передать null - вернет все фильмы.
     * @param count количество отзывов для каждого фильма, если передать null - вернет 10 отзывов.
     * @return Коллекция отзывов для указанного фильма.
     */
    @Override
    public Collection<Review> getReviews(Integer filmId, Integer count) {
        return reviews.getReviews(filmId, count);
    }

    /**
     * Добавляет Like к отзыву.
     *
     * @param reviewId id отзыва для добавления оценки.
     * @param userId id пользователя, который оценивает фильм.
     * @throws NotFoundException Если отзыв с указанным id не найден или пользователь с указанным id не найден.
     */
    @Override
    public Review addLike(Integer reviewId, Integer userId) {
        if (reviews.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Не найден отзыв с id=" + reviewId);
        }
        if (users.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id=" + userId);
        }
        return reviews.addLike(reviewId, userId);
    }

    /**
     * Добавляет Dislike к отзыву.
     *
     * @param reviewId id отзыва для добавления оценки.
     * @param userId   id пользователя, который оценивает фильм.
     * @throws NotFoundException Если отзыв с указанным id не найден или пользователь с указанным id не найден.
     */
    @Override
    public Review addDisLike(Integer reviewId, Integer userId) {
        if (reviews.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Не найден отзыв с id=" + reviewId);
        }
        if (users.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id=" + userId);
        }
        return reviews.addDisLike(reviewId, userId);
    }

    /**
     * Удаляет оценку отзыва фильма пользователем.
     *
     * @param reviewId id отзыва для удаления оценки.
     * @param userId id пользователя, который удаляет оценку фильма.
     * @throws NotFoundException Если отзыв или пользователь с указанным id не найдены.
     */
    @Override
    public Review deleteFeedback(Integer reviewId, Integer userId) {
        if (reviews.containsFeedback(reviewId, userId)) {
            return reviews.deleteFeedback(reviewId, userId);
        } else {
            throw new NotFoundException("Для отзыва с id=" + reviewId +
                    " не найдена оценка пользователя с id=" + userId);
        }
    }
}
