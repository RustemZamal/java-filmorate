package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFountException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {

    private final static String SQL_INSERT_INTO_FILM =
            "INSERT INTO film (name, description, release_date, duration, mpa_rating) " +
                    "VALUES (:name, :description, :release_date, :duration, :mpa_rating)";

    private final static String SQL_UPDATE_FILM =
            "UPDATE FILM SET name = :name, description = :description, release_date = :release_date," +
                    " duration = :duration, mpa_rating = :mpa_rating WHERE film_id = :film_id";

    private final static String SQL_GET_ALL_FILMS = "SELECT * FROM film";

    private final static String SQL_GET_FILM_BY_ID = "SELECT * FROM film WHERE film_id = :film_id";

    private final static String SQL_DELETE_FILM_BY_ID = "DELETE FROM film WHERE film_id = :film_id";

    private final static String SQL_GET_POPULAR = "SELECT fm.* " +
            "FROM film AS fm " +
            "LEFT JOIN like_to_film AS lf ON fm.film_id = lf.film_id " +
            "GROUP BY fm.film_id " +
            "ORDER BY COUNT(lf.user_id) DESC " +
            "LIMIT :LIMIT";


    private final static String SQL_GET_POPULAR_DATA_AND_GENRE =
            "SELECT fm.* " +
                    "FROM film AS fm " +
                    "LEFT JOIN like_to_film AS lf ON fm.film_id = lf.film_id " +
                    "LEFT JOIN film_genre AS fg ON fm.film_id = fg.film_id " +
                    "LEFT JOIN genre AS gi ON fg.genre_id = gi.genre_id " +
                    "WHERE gi.genre_id = %s AND YEAR(fm.release_date) = %s " +
                    "GROUP BY fm.film_id " +
                    "ORDER BY COUNT(lf.user_id) DESC " +
                    "LIMIT %s ";


    private final static String SQL_GET_POPULAR_DATA =
            "SELECT fm.* " +
                    "FROM film AS fm " +
                    "LEFT JOIN like_to_film AS lf ON fm.film_id = lf.film_id " +
                    "WHERE YEAR(fm.release_date) = %s " +
                    "GROUP BY fm.film_id " +
                    "ORDER BY COUNT(lf.user_id) DESC " +
                    "LIMIT %s ";

    private final static String SQL_GET_POPULAR_GENRE =
            "SELECT fm.* " +
                    "FROM film AS fm " +
                    "LEFT JOIN like_to_film AS lf ON fm.film_id = lf.film_id " +
                    "LEFT JOIN film_genre AS fg ON fm.film_id = fg.film_id " +
                    "LEFT JOIN genre AS gi ON fg.genre_id = gi.genre_id " +
                    "WHERE gi.genre_id = %s " +
                    "GROUP BY fm.film_id " +
                    "ORDER BY COUNT(lf.user_id) DESC " +
                    "LIMIT %s ";


    private final static String SQL_FIND_BY_PARAMETER = "SELECT f.* " +
            "FROM FILM f " +
            "LEFT JOIN like_to_film lf ON f.film_id = lf.film_id " +
            "LEFT JOIN FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID " +
            "LEFT JOIN DIRECTOR D on D.ID = FD.DIRECTOR_ID " +
            "WHERE %s " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(lf.user_id) DESC";

    private final MpaRatingDao mpaRating;
    private final GenreDao genreDao;

    private  final LikesDao likesDao;

    private final FilmGenreDao filmGenreDao;

    private final FilmDirectorDao filmDirectorDao;

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
        log.debug("Добвлен новый фильм [{}]", film);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreDao.insertToFilmGenre(film.getId(), genre.getId());
            }
        }

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                filmDirectorDao.insertToFilmDirector(film.getId(), director.getId());
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
            throw new FilmNotFountException(String.format("Фильм с ID=%d не существует", film.getId()));
        }


        // Надо переделать !!!
        filmGenreDao.deleteRowFromFilmGenre(film.getId());
        filmDirectorDao.deleteRowFromFilmDirector(film.getId());


        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreDao.insertToFilmGenre(film.getId(), genre.getId());
            }
        }

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                filmDirectorDao.insertToFilmDirector(film.getId(), director.getId());
            }
        }
        log.debug("Обновлен фильм с id={}", film.getId());
        return findFilmById(film.getId());
    }

    @Override
    public List<Film> findAllFilms() {
        log.debug("Запрошен список всех фильмов.");
        return namedParameterJdbcTemplate.query(SQL_GET_ALL_FILMS, this::makeFilm);
    }

    @Override
    public Film findFilmById(Long id) {
        log.debug("Запрошен фильм с id={}", id);
        return namedParameterJdbcTemplate.query(
                        SQL_GET_FILM_BY_ID, new MapSqlParameterSource("film_id", id), this::makeFilm)
                .stream()
                .findFirst()
                .orElseThrow(() -> new FilmNotFountException(String.format("Фильма с id=%d не существует", id)));
    }

    @Override
    public List<Film> getPopularFilm(Integer count) {
        log.debug("Запрошен список популярных фильмов по кол-во лайков, в ограничинии-{} фильм(ов).", count);
        return namedParameterJdbcTemplate.query(SQL_GET_POPULAR, new MapSqlParameterSource("LIMIT", count), this::makeFilm);
    }

    @Override
    public List<Film> findByParameter(String query, String by) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("name", query);
        if (by.contains("director") && by.contains("title")) {
            String SQL_FIND_ANYWAY = String.format( // LOWER(f.name) LIKE LOWER('%upDatE%') OR LOWER(D.NAME) LIKE LOWER('%upDatE%')
                    SQL_FIND_BY_PARAMETER, "LOWER(f.name) LIKE LOWER(?) OR LOWER(D.name) LIKE LOWER(?)");
            List<Film> films = namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL_FIND_ANYWAY, this::makeFilm, "%" + query + "%", "%" + query + "%");
            return films;

        } else if (by.contains("title")) {
            String SQL = String.format(SQL_FIND_BY_PARAMETER, "LOWER(f.name) LIKE LOWER(?)"); // LOWER(NAME) LIKE LOWER('%FiL%')
            List<Film> films = namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL, this::makeFilm, "%" + query + "%");
            return films;

        } else if (by.contains("director")) {
            String SQL_BY_DIRECTOR = String.format(SQL_FIND_BY_PARAMETER, "LOWER(D.name) LIKE LOWER(?)");
            List<Film> films = namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL_BY_DIRECTOR, this::makeFilm, "%" + query + "%");
            return films;
        }
        return List.of();
    }

    @Override
    public void deleteFilmById(Long id) {
        namedParameterJdbcTemplate.update(SQL_DELETE_FILM_BY_ID, new MapSqlParameterSource());
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = mpaRating.getMpaById(resultSet.getInt("mpa_rating"));
        Set<Genre> genre = new HashSet<>(genreDao.getGenreByFilm(resultSet.getLong("film_id")));
        Set<Long> likes = likesDao.findLikesByFilm(resultSet.getLong("film_id"));

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
    public List <Film> getPopularFilmByDateAndGenre (Integer count, Integer genreId, Integer year) {
        log.debug(String.format("Запрошен список популярных фильмов по жанру и году. Где год: %s , жанр ID: %s, лимит: %s ",
                year, genreId, count));
        String SQL = "s";

        if (genreId != -1 && year != -1){
            SQL = String.format(SQL_GET_POPULAR_DATA_AND_GENRE, genreId, year, count);
        } else if (year != -1) {
            SQL = String.format(SQL_GET_POPULAR_DATA, year, count);
        } else {
            SQL = String.format(SQL_GET_POPULAR_GENRE, genreId, count);
        }
        System.out.println(SQL);
        return namedParameterJdbcTemplate.query(SQL, this::makeFilm);
    }
}
