# java-filmorate
Учебный групповой проект. Группа №11 (когорта 53) 



## - add-remove-endpoint - Удаление фильмов и пользователей. 2 SP

### Описание задачи
В приложению нужно добавить функциональность для удаления фильма и пользователя по идентификатору.

### API
DELETE /users/{userId}<br>
Удаляет пользователя по идентификатору.

DELETE /films/{filmId}<br>
Удаляет фильм по идентификатору.


=======
## -develop- Реализация новых функциональностей

Фич-лист того, что требуется добавить в приложение:<br>
1. Функциональность «Отзывы».<br>
2. Функциональность «Поиск».<br>
3. Функциональность «Общие фильмы».<br>
4. Функциональность «Рекомендации».<br>
5. Функциональность «Лента событий».<br>

Проджект хочет пойти дальше и добавить ещё пару полезных фич. По его мнению, они не займут много времени на этапе разработки, 
но создадут вау-эффект на презентации проекта.<br>
- Функциональность «Популярные фильмы», ко торая предусматривает вывод самых любимых у зрителей фильмов по жанрам и годам.
- Функциональность «Фильмы по режиссёрам», которая предполагает добавление к фильму информации о его режиссёре.
- Функциональность «Удаление фильмов и пользователей», которая предусматривает удаление фильма или пользователя по идентификатору.

## Первоначальная схема базы данных.

![схема базы данных](/schema.png)

### Описание таблиц базы данных

1. **users** - таблица описания пользователей.<br>
поля:
    - первичный ключ *id* - идентификатор подьзователя;
    - *email* - адрес электронно почты пользователя;
    - *login* - логин пользоателя;
    - *name* - имя пользователя;
    - *birthday* - дата рождения пользователя;
    
    <br>
2. **friends** - таблица связи с "друзьями" пользователя.<br>
   поля:
   - *user_id* - идентификатор пользователя (отсылает к таблице *users*) - идентификатор пользователя;
   - *friend_id* - идентификатор друга (отсылает к таблице *users*) - идентификатор пользователя;
   - *confirmed* - флаг подтвержденной дружбы (если "дружба" двусторонняя);
   
   <br>
3. **genre** - таблица описания жанро фильма.<br>
   поля:
   -  первичный ключ *id* - идентификатор жанра;
   - *name* - наименование жанра;

   <br>
4. **MPA** - таблица описания рейтингов Ассоциации кинокомпаний (MPA).<br>
   поля: 
   - первичный ключ *id* - идентификатор рейтинга;
   - *name* - буквенный код рейтинга (G, PG, PG-13, R, NC-17);
   - *description* - описание рейтинга;
   
   <br>
5. **films** - таблица описания фильмов. <br>
   поля:
   - первичный ключ *id* - идентификатор фильма;
   - *name* - название фильма;
   - *description* - описание фильма;
   - *releaseDAte* - бата выпуска фильма;
   - *len_min* - длительность фильма в минутах; 
   - *MPA_id* -рейтинг MPA. (отсылает к таблице *MPA*) - идентификатор рейтинга;
   
   <br>
6. **film_genre** - таблица определения жанров фильма.<br>
   поля:
   - *film_id* - идентификатор фильма (отсылает к таблице *films*);
   - *genre_id* - идентификатор жанра (отсылает к таблице *genre*);
   
   <br>
7. **likes** - таблица "лайков" пользователей.<br>
   поля:
   - *user_id* - идентификатор пользователя (отсылает к таблице *users*);
   - *film_id* - идентификатор фильма (отсылает к таблице *films*);

