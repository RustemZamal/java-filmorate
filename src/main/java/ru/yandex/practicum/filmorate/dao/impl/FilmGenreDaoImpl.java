package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final static String SQL_INSERT_FILM_GENRE =
            "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";

    private final static String SQL_DELETE_ROW_FILM_GENRE = "DELETE FROM film_genre WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertToFilmGenre(Long filmId, Integer genreId) {
        log.debug("Вставлена записть c фильмом id={}, жанром id{}, в таблицу film_genre.", filmId, genreId);
        jdbcTemplate.update(SQL_INSERT_FILM_GENRE, filmId, genreId);
    }

    @Override
    public void deleteRowFromFilmGenre(Long filmId) {
        log.debug("Удалена запись с фильмом id={} с таблицы film_genre.", filmId);
        jdbcTemplate.update(SQL_DELETE_ROW_FILM_GENRE, filmId);
    }
}
