# java-filmorate

### ER-диаграмма и примеры запросов
#### [created on the website](https://dbdiagram.io/d/63ec79b2296d97641d810b7c)
---
![Модель базы данных](ER-diagram-filmorate.pdf)

<details>
  <summary><h3>Для пользователей:</h3></summary>
  
* создание пользователя
```SQL
INSERT INTO users (email, login, name, birthday)
VALUES ( ?, ?, ?, ? );
```
* редактирование пользователя
```SQL
UPDATE users
SET email = ?,
    login = ?,
    name = ?,
    birthday = ?
WHERE user_id = ?
```
* получение списка всех пользователей
```SQL
SELECT *
FROM users
```
* получение информации о пользователе по его `id`
```SQL
SELECT *
FROM users
WHERE user_id = id?
```
* добавление в друзья
```SQL
INSERT INTO users_friends (user_id, friend_id, status)
VALUES (?, ?, ?)
```
* удаление из друзей
```SQL
DELETE
FROM users_friends
WHERE user_id = id? AND friend_id = id?
```
* возвращает список пользователей, являющихся друзьями другого пользователя
```SQL
SELECT u.*
FROM USERS AS u
INNER JOIN FRIENDSHIP F ON U.USER_ID = F.FRIEND_ID
WHERE F.USER_ID = 3
UNION
SELECT U.*
FROM USERS AS U
INNER JOIN FRIENDSHIP F on U.USER_ID = F.USER_ID
WHERE F.FRIEND_ID = 3 AND STATUS = True
```
* список друзей, общих с другим пользователем
```SQL
SELECT *
FROM USERS WHERE USER_ID IN (SELECT CASE
WHEN (user_id = ? AND friend_id != ?) THEN friend_id
WHEN (user_id != ? AND friend_id = ?) THEN user_id
END
FROM FRIENDSHIP
INTERSECT
SELECT CASE
WHEN (user_id = ? AND friend_id != ?) THEN friend_id
WHEN (user_id != ? AND friend_id = ?) THEN user_id
END
FROM FRIENDSHIP);
```

</details>
<details>
  <summary><h3>Для фильмов:</h3></summary>

* создание фильма
```SQL
INSERT INTO film (name, description, release_date, duration, mpa_rating)
VALUES (?, ?, ?, ?, ?)
```
* редактирование фильма
```SQL
UPDATE film
SET name = ?,
    description = ?,
    release_date = ?,
    duration = ?,
    mpa_rating = ?
WHERE film_id = ?
```
* получение списка всех фильмов с полной информацией
```SQL
SELECT film.*,
       COUNT(like_to_film.user_id) AS likes,
       G2.GENRE_NAME,
       MR.MPA_NAME
FROM film
LEFT JOIN like_to_film ON film.film_id = like_to_film.film_id
LEFT JOIN FILM_GENRE FG on FILM.FILM_ID = FG.FILM_ID
LEFT JOIN GENRE G2 on G2.GENRE_ID = FG.GENRE_ID
LEFT JOIN MPA_RATING MR on MR.MPA_ID = FILM.MPA_RATING
GROUP BY film.film_id, G2.GENRE_NAME
ORDER BY film.film_id;
```
* получение информации о фильме по его `id`
```SQL
SELECT film.*,
       COUNT(like_to_film.user_id) AS likes,
       G2.GENRE_NAME,
       MR.MPA_NAME
FROM film
LEFT JOIN like_to_film ON film.film_id = like_to_film.film_id
LEFT JOIN FILM_GENRE FG on FILM.FILM_ID = FG.FILM_ID
LEFT JOIN GENRE G2 on G2.GENRE_ID = FG.GENRE_ID
LEFT JOIN MPA_RATING MR on MR.MPA_ID = FILM.MPA_RATING
WHERE FG.FILM_ID = 2
GROUP BY film.film_id, G2.GENRE_NAME
```
* пользователь ставит лайк фильму
```SQL
INSERT INTO like_to_film (film_id, user_id)
VALUES (?, ?)
```
* пользователь удаляет лайк
```SQL
DELETE
FROM like_to_film
WHERE film_id = ? AND user_id = ?
```
* возвращает список из `n` фильмов по количеству лайков
`n` - кол-во фильмов
```SQL
SELECT f.name,
       COUNT(lf.user_id) AS likes
FROM film AS f
LEFT JOIN likes_to_films AS lf ON f.film_id=lf.film_id
GROUP BY f.film_id, f.name
ORDER BY likes DESC, f.name
LIMIT ?
```
* получения списка с названием фильмов и жанра
```SQL
SELECT f.name,
       g.name
FROM film AS f
INNER JOIN film_genre AS fg ON f.film_id=fg.film_id
INNER JOIN genre AS g ON fg.genre_id=g.genre_id;
```
* получения списка имен пользоветелей кто поставил лайк фильму и названия фильма
```SQL
SELECT us.name AS name_user,
       f.name AS name_movie      
FROM films AS f
INNER JOIN like_to_film AS lf ON f.film_id=lf.film_id
INNER JOIN users AS us ON lf.user_id=us.user_id
ORDER BY us.name
```

</details>
<details>
  <summary><h3>Для жанров:</h3></summary>
  
* получение списка всех жанров
```SQL
SELECT *
FROM genre
```
  
</details>

