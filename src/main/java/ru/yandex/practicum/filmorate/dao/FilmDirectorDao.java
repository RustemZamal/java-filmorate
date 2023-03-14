package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface FilmDirectorDao {

    Director addDirector(Director director);

    void deleteDirector(Long directorId);

    Director updateDirector(Director director);

    Director findDirectorById(Long directorId);

    List<Director> findAllDirectors();

    void insertToFilmDirector(Long filmId, Long directorId);

    void deleteRowFromFilmDirector(Long filmId);

    Collection<Director> getDirectorByFilm(Long filmId);
}
