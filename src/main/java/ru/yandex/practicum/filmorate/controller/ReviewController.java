package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@Validated(Marker.OnBasic.class) @RequestBody Review review) {
        log.info("Добавляем новый отзыв: {}.", review.toString());
        return reviewService.addReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@PathVariable("id") @Pattern(regexp = "\\d+") String reviewIdStr) {
        log.debug("Удаляем отзыв id={}.", Integer.parseInt(reviewIdStr));
        reviewService.deleteReview(Integer.parseInt(reviewIdStr));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@Validated(Marker.OnUpdate.class) @RequestBody Review updReview) {
        log.info("Обновляем информацию об отзыве id={} : {}", updReview.getReviewId(), updReview);
        return reviewService.updateReview(updReview);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review findReview(@PathVariable("id") @Pattern(regexp = "\\d+") String reviewIdStr) {
        log.info("Ищем отзыв id={}.", Integer.parseInt(reviewIdStr));
        return reviewService.getReviewById(Integer.parseInt(reviewIdStr));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> getReviews(@RequestParam(required = false) Integer filmId,
                                         @RequestParam(defaultValue = "10") Integer count) {
        log.info("Получаем список {} отзывов для фильма с id = {}.", count, filmId);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review addLike(@PathVariable("id") Integer reviewId,
                          @PathVariable("userId") Integer userId) {
        log.debug("Добавляем \"лайк\" отзыву {}, от пользователя {}.", reviewId, userId);
        return reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review addDisLike(@PathVariable("id") Integer reviewId,
                             @PathVariable("userId") Integer userId) {
        log.debug("Добавляем \"дизлайк\" отзыву {}, от пользователя {}.", reviewId, userId);
        return reviewService.addDisLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review deleteLike(@PathVariable("id") Integer reviewId,
                             @PathVariable("userId") Integer userId) {
        log.debug("Удаляем \"лайк\" у отзыва {}, от пользователя {}.", reviewId, userId);
        return reviewService.deleteFeedback(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review deleteDisLike(@PathVariable("id") Integer reviewId,
                                @PathVariable("userId") Integer userId) {
        log.debug("Удаляем \"дизлайк\" у отзыва {}, от пользователя {}.", reviewId, userId);
        return reviewService.deleteFeedback(reviewId, userId);
    }
}
