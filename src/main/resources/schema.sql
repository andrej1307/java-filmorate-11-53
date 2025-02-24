-- Создаем таблицу пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL,
                                     login VARCHAR(40) NOT NULL,
                                     name VARCHAR(40) NOT NULL,
                                     birthday DATE NOT NULL
);

-- Создаем таблицу друзей
CREATE TABLE IF NOT EXISTS friends (
                                       user_id INTEGER NOT NULL REFERENCES users(id),
                                       friend_id INTEGER NOT NULL REFERENCES users(id),
                                       confirmed BOOLEAN NOT NULL DEFAULT FALSE,
                                       PRIMARY KEY (user_id, friend_id)
);

-- Создаем справочник жанров фильма
CREATE TABLE IF NOT EXISTS genres (
                                      id INTEGER PRIMARY KEY,
                                      name VARCHAR(40) NOT NULL
);

-- Создаем справочник рейтинга MPA
CREATE TABLE IF NOT EXISTS MPA (
                                   id INTEGER PRIMARY KEY,
                                   name VARCHAR(8) NOT NULL,
                                   description VARCHAR(80)
);

-- Создаем таблицу описания фильма
CREATE TABLE IF NOT EXISTS films (
                                     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(40) NOT NULL,
                                     description VARCHAR(200),
                                     releaseDate DATE,
                                     len_min INTEGER,
                                     MPA_id INTEGER NOT NULL REFERENCES MPA(id)
);

-- Создаем таблицу описания жанра фильма
CREATE TABLE IF NOT EXISTS films_genres (
                                            film_id INTEGER NOT NULL REFERENCES films(id),
                                            genre_id INTEGER NOT NULL REFERENCES genres(id),
                                            PRIMARY KEY (film_id, genre_id)
);

-- Создаем таблицу "лайков" к фильмам
CREATE TABLE IF NOT EXISTS likes (
                                     user_id INTEGER NOT NULL REFERENCES users(id),
                                     film_id INTEGER NOT NULL REFERENCES films(id),
                                     PRIMARY KEY (user_id, film_id)
);
