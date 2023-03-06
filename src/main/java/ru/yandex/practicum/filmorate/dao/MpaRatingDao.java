package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRatingDao {

    /**
     *
     * @return Возврщает все Mpa рейтинги.
     */
    List<Mpa> getAllMpa();

    /**
     *
     * @param id идентификатор Mpa рейтинга.
     * @return Возвращает Mpa рейтинг по его id.
     */
    Mpa getMpaById(int id);
}
