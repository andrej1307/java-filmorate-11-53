-- Заполняем справочник жанров
MERGE INTO genres (id, name)
    VALUES ( 1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

-- Заполняем справочник рейтингов MPA
MERGE INTO MPA (id, name, description)
    VALUES (1, 'G', 'у фильма нет возрастных ограничений'),
           (2, 'PG', 'детям рекомендуется смотреть фильм с родителями'),
           (3, 'PG-13', 'детям до 13 лет просмотр не желателен'),
           (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
           (5, 'NC-17', 'лицам до 18 лет просмотр запрещён');

-- Создаем тестовоых пользователей
INSERT INTO users (email, login, name, birthday)
VALUES ( 'test@test.com', 'testLogin', 'testName', '2001-9-22' ),
       ('user1@test.com', 'userTest1', 'userNane1', '2001-01-01'),
       ('user2@test.com', 'userTest2', 'userNane2', '2002-02-02'),
       ('user3@test.com', 'userTest3', 'userNane3', '2003-03-03');

-- Создаем тестовые фильмы
INSERT INTO films (name, description, releasedate, len_min, mpa_id)
VALUES ( 'TestFilmName',  'TestFilmDescription', '2001-02-03', 51, 1),
       ( 'TestFilmName2',  'TestFilmDescription2', '2002-03-04', 62, 2),
       ( 'TestFilmName3',  'TestFilmDescription3', '2003-04-05', 73, 3),
       ( 'TestFilmName4',  'TestFilmDescription4', '2004-05-06', 92, 4);

INSERT INTO films_genres (film_id, genre_id)
VALUES ( 1, 1 );

