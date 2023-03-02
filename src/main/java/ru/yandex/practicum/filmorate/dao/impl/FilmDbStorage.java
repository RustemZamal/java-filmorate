package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFountException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {

    private final static String SQL_INSERT_INTO_FILM =
            "INSERT INTO film (name, description, release_date, duration, mpa_rating) " +
            "VALUES (:name, :description, :release_date, :duration, :mpa_rating)";

    private final static String SQL_INSERT_FILM_GENRE =
            "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (:film_id, :genre_id)";

    String SQL_UPDATE_FILM =
            "UPDATE FILM SET name = :name, description = :description, release_date = :release_date," +
                    " duration = :duration, mpa_rating = :mpa_rating WHERE film_id = :film_id";

    String SQL_DELETE_ROW_FILM_GENRE = "DELETE FROM film_genre WHERE film_id = :film_id";

    String SQL_GET_ALL_FILMS = "SELECT * FROM film";

    String SQL_GET_FILM_BY_ID = "SELECT * FROM film WHERE film_id = :film_id";

    String SQL_GET_LIKES = "SELECT USER_ID FROM LIKE_TO_FILM WHERE FILM_ID = ?";

    String SQL_DELETE_FILM_BY_ID = "DELETE FROM film WHERE film_id = ?";

    private final MpaRatingDao mpaRating;
    private final GenreDao genreDao;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parSource = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_rating", film.getMpa().getId());
        namedParameterJdbcTemplate.update(SQL_INSERT_INTO_FILM, parSource, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                SqlParameterSource parInsert = new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", genre.getId());
                namedParameterJdbcTemplate.update(SQL_INSERT_FILM_GENRE, parInsert);
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        SqlParameterSource parUpdate = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_rating", film.getMpa().getId());
        int check = namedParameterJdbcTemplate.update(SQL_UPDATE_FILM, parUpdate);

        if (check == 0) {
            throw new FilmNotFountException(String.format("Фильм с ID=%d не существует", film.getId()));        }

        if(film.getGenres() == null || film.getGenres().isEmpty()) {
            namedParameterJdbcTemplate.update(
                    SQL_DELETE_ROW_FILM_GENRE, new MapSqlParameterSource("film_id", film.getId()));
            return findFilmById(film.getId());
        }

        namedParameterJdbcTemplate.update(
                SQL_DELETE_ROW_FILM_GENRE, new MapSqlParameterSource("film_id", film.getId()));


        for (Genre genre : film.getGenres()) {
            SqlParameterSource parInsert = new MapSqlParameterSource()
                    .addValue("film_id", film.getId())
                    .addValue("genre_id", genre.getId());
            namedParameterJdbcTemplate.update(SQL_INSERT_FILM_GENRE, parInsert);
        }

        return findFilmById(film.getId());
    }

    @Override
    public List<Film> findAllFilms() {
        return namedParameterJdbcTemplate.query(SQL_GET_ALL_FILMS, this::makeFilm);
    }

    @Override
    public Film findFilmById(Long id) {
        return namedParameterJdbcTemplate.query(
                SQL_GET_FILM_BY_ID, new MapSqlParameterSource("film_id", id), this::makeFilm)
                .stream()
                .findFirst()
                .orElseThrow(() -> new FilmNotFountException(String.format("Фильма с id=%d не существует", id)));
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = mpaRating.getMpaById(resultSet.getInt("mpa_rating"));
        Set<Genre> genre = new LinkedHashSet<>(genreDao.getGenreByFilm(resultSet.getLong("film_id")));
        Set<Long> likes = new HashSet<>(namedParameterJdbcTemplate.getJdbcOperations().query(
                SQL_GET_LIKES, (rs, rowN) -> rs.getLong("user_id"), resultSet.getLong("film_id")));

        return Film.builder()
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .id(resultSet.getLong("film_id"))
                .mpa(mpa)
                .genres((genre))
                .likesUserId(likes)
                .build();
    }

    @Override
    public void deleteFilmById(Long id) {
        namedParameterJdbcTemplate.update(SQL_DELETE_FILM_BY_ID, new MapSqlParameterSource());
    }

    @Override
    public void deleteAllFilms() {

    }

}
