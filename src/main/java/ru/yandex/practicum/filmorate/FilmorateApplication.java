package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения рейтинга фильмов.
 */
@SpringBootApplication
public class FilmorateApplication {

	/**
	 * Запуск приложения.
	 *
	 * @param args - параметры запуска.
	 */
	public static void main(final String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}
}
