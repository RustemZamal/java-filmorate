package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface FilmDirectorDao {

    /**
     *
     * @param director сущность режиссер.
     * @return Возвращает созданый объект режиссер.
     */
    Director addDirector(Director director);

    /**
     * Эндпоинт по удалению режиссера по его id.
     * @param directorId идентификатор режиссера.
     */
    void deleteDirector(Long directorId);

    /**
     *
     * @param director сущность режиссер.
     * @return Возвращает обновленый объект режиссер.
     */
    Director updateDirector(Director director);

    /**
     *
     * @param directorId идентификатор режиссера
     * @return Возращает режиссера по его id.
     */
    Director findDirectorById(Long directorId);

    /**
     *
     * @return Возвращает всех режиссеров.
     */
    List<Director> findAllDirectors();

    /**
     * Метод по добавлению filmId и directorId в таблицу film_director.
     * @param filmId идентификатор фильма.
     * @param directorId идентификатор режиссера.
     */
    void insertToFilmDirector(Long filmId, Long directorId);

    /**
     * Метод по удалиению фильма из таблици film_director.
     * @param filmId идентификатор фильма.
     */
    void deleteRowFromFilmDirector(Long filmId);

    /**
     *
     * @param filmId идентификатор фильма.
     * @return Возвращает режиссеров по filmId.
     */
    Collection<Director> getDirectorByFilm(Long filmId);
}
