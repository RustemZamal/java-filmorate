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
     *
     * @param filmId идентификатор фильмаа.
     * @return Возвращает id пользователей которые поставили лайк фильму с идентификатором  filmId.
     */
    Set<Long> findLikesByFilm(Long filmId);
}
