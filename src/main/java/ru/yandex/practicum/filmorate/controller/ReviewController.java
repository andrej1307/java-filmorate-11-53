package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Добавляет новый отзыв.
     *
     * @param review отзыв, который нужно добавить
     * @return добавленный отзыв
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@Validated(Marker.OnBasic.class) @RequestBody Review review) {
        log.info("Добавляем новый отзыв: {}.", review.toString());
        return reviewService.addReview(review);
    }

    /**
     * Удаляет отзыв по его id.
     *
     * @param reviewIdStr id отзыва, который нужно удалить
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@PathVariable("id") @Pattern(regexp = "\\d+") String reviewIdStr) {
        log.debug("Удаляем отзыв id={}.", Integer.parseInt(reviewIdStr));
        reviewService.deleteReview(Integer.parseInt(reviewIdStr));
    }

    /**
     * Обновляет информацию об отзыве.
     *
     * @param updReview отзыв с обновленной информацией
     * @return обновленный отзыв
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@Validated(Marker.OnUpdate.class) @RequestBody Review updReview) {
        log.info("Обновляем информацию об отзыве id={} : {}", updReview.getReviewId(), updReview);
        return reviewService.updateReview(updReview);
    }

    /**
     * Получает отзыв по его id.
     *
     * @param reviewIdStr id отзыва, который нужно найти
     * @return найденный отзыв
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review findReview(@PathVariable("id") @Pattern(regexp = "\\d+") String reviewIdStr) {
        log.info("Ищем отзыв id={}.", Integer.parseInt(reviewIdStr));
        return reviewService.getReviewById(Integer.parseInt(reviewIdStr));
    }

    /**
     * Получает список отзывов по фильму с лимитом на количество отзывов.
     *
     * @param filmId id фильма, отзывы которого нужно получить
     * @param count количество отзывов, которое нужно получить
     * @return список отзывов
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> getReviews(@RequestParam(required = false) Integer filmId,
                                         @RequestParam(defaultValue = "10") Integer count) {
        log.info("Получаем список {} отзывов для фильма с id = {}.", count, filmId);
        return reviewService.getReviews(filmId, count);
    }

    /**
     * Добавляет лайк к отзыву.
     *
     * @param reviewId id отзыва
     * @param userId id пользователя, который поставил лайк
     * @return отзыв с обновленной информацией о лайках
     */
    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review addLike(@PathVariable("id") Integer reviewId,
                          @PathVariable("userId") Integer userId) {
        log.debug("Добавляем \"лайк\" отзыву {}, от пользователя {}.", reviewId, userId);
        return reviewService.addLike(reviewId, userId);
    }

    /**
     * Добавляет дизлайк к отзыву.
     *
     * @param reviewId id отзыва
     * @param userId id пользователя, который поставил дизлайк
     * @return отзыв с обновленной информацией о дизлайках
     */
    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review addDisLike(@PathVariable("id") Integer reviewId,
                             @PathVariable("userId") Integer userId) {
        log.debug("Добавляем \"дизлайк\" отзыву {}, от пользователя {}.", reviewId, userId);
        return reviewService.addDisLike(reviewId, userId);
    }

    /**
     * Удаляет лайк у отзыва.
     *
     * @param reviewId id отзыва
     * @param userId id пользователя, который удалил лайк или дизлайк
     * @return отзыв с обновленной информацией о лайках или дизлайках
     */
    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review deleteLike(@PathVariable("id") Integer reviewId,
                             @PathVariable("userId") Integer userId) {
        log.debug("Удаляем \"лайк\" у отзыва {}, от пользователя {}.", reviewId, userId);
        return reviewService.deleteFeedback(reviewId, userId);
    }

    /**
     * Удаляет дизлайк у отзыва.
     *
     * @param reviewId id отзыва
     * @param userId id пользователя, который удалил лайк или дизлайк
     * @return отзыв с обновленной информацией о лайках или дизлайках
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review deleteDisLike(@PathVariable("id") Integer reviewId,
                                @PathVariable("userId") Integer userId) {
        log.debug("Удаляем \"дизлайк\" у отзыва {}, от пользователя {}.", reviewId, userId);
        return reviewService.deleteFeedback(reviewId, userId);
    }
}
