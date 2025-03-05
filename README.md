# java-filmorate
Учебный групповой проект. Группа №11 (когорта 53) 

## - add-director - Добавление режиссёров в фильмы. 4 SP

### Описание задачи
В информацию о фильмах должно быть добавлено имя режиссёра. После этого должна появиться следующая функциональность.

Вывод всех фильмов режиссёра, отсортированных по количеству лайков.<br>
Вывод всех фильмов режиссёра, отсортированных по годам.

### API

GET /films/director/{directorId}?sortBy=[year,likes]<br>
Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.

POST /films<br>
{<br>
&ensp;"name": "New film",<br>
&ensp;"releaseDate": "1999-04-30",<br>
&ensp;"description": "New film about friends",<br>
&ensp;"duration": 120,<br>
&ensp;"mpa": { "id": 3},<br>
&ensp;"genres": [{ "id": 1}],<br>
&ensp;"director": [{ "id": 1}]<br>
}<br>

GET /directors - Список всех режиссёров

GET /directors/{id}- Получение режиссёра по id

POST /directors - Создание режиссёра

PUT /directors - Изменение режиссёра<br>
{<br>
&ensp;"id": 1,<br>
&ensp;"name": "New director"<br>
}<br>

DELETE /directors/{id} - Удаление режиссёра