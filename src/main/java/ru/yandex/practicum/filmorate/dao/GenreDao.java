package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GenreDao {

    /**
     *
     * @param id идентификатор жанра фильма.
     * @return Возвращает жанр по id.
     */
    Genre getGenreById(int id);

    /**
     *
     * @return Возварщает все жанры фильмов.
     */
    List<Genre> getAllGenre();

    /**
     *
     * @param id идентификатор фильма.
     * @return Возвращает жанры, которые относятся к фильму с id.
     */
    Collection<Genre> getGenreByFilm(Long id);
}
