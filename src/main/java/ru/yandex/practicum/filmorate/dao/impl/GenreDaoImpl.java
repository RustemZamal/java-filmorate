package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GenreDaoImpl implements GenreDao {

    String SQL_GET_GENRE_BY_ID = "SELECT * FROM GENRE WHERE GENRE_ID = ?";

    String SQL_GET_ALL_GENRE = "SELECT * FROM genre ORDER BY GENRE_ID ASC";

    String SQL_GET_GENRE_BY_FILM = "SELECT g.* FROM genre g " +
            "INNER JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        return jdbcTemplate.query(SQL_GET_GENRE_BY_ID, this::makeGenre, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new GenreNotFoundException(String.format("Жанр с id=%d не существует", id)));
    }


    @Override
    public List<Genre> getAllGenre() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRE, this::makeGenre);
    }

    @Override
    public Collection <Genre> getGenreByFilm(Long filmId) {
        return jdbcTemplate.query(SQL_GET_GENRE_BY_FILM, this::makeGenre, filmId);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name"));
    }
}
