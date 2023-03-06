package ru.yandex.practicum.filmorate.dao;

public interface FilmGenreDao {

    void insertToFilmGenre(Long filmId, Integer genreId);

    void deleteRowFromFilmGenre(Long filmId);
}
