package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;

import java.util.List;
import java.util.Set;

public interface LikesDao {

    /**
     * Метод позволяющий пользователем ставить лайки фильмам.
     * @param id фильм, которому ставиться лайк.
     * @param userId пользователь, который ставит лайк.
     */
    void putLike(Long id, Long userId);

    /**
     * Метод по удалению лайка фильму пользователем.
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    void removeLike(Long id, Long userId);

    /**
     * Метод по нахождению популярных по количеству лайков фильмов.
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список популярных фильмов согласна параметру count.
     */
    List<Film> getPopularFilm(Integer count);

}
