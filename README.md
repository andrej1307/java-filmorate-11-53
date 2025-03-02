# java-filmorate
Учебный групповой проект. Группа №11 (когорта 53) 

## Начальная схема базы данных.

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
