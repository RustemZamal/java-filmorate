package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
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

    private final static String SQL_FIND_BY_PARAMETER = "SELECT f.* " +
            "FROM FILM f " +
            "LEFT JOIN like_to_film lf ON f.film_id = lf.film_id " +
            "LEFT JOIN FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID " +
            "LEFT JOIN DIRECTOR D on D.ID = FD.DIRECTOR_ID " +
            "WHERE %s " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(lf.user_id) DESC";

    private final static String SQL_FIND_BY_YEAR = "SELECT f.* " +
            "FROM FILM f " +
            "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
            "WHERE fd.director_id = ? " +
            "ORDER BY f.RELEASE_DATE";

    private final static String SQL_FIND_BY_LIKES = "SELECT f.* " +
            "FROM film f " +
            "LEFT JOIN like_to_film lf ON f.film_id = lf.film_id " +
            "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
            "WHERE fd.director_id = ? " +
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
        addGenreAndDirector(film);

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

        filmGenreDao.deleteRowFromFilmGenre(film.getId());
        filmDirectorDao.deleteRowFromFilmDirector(film.getId());
        addGenreAndDirector(film);
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
        if (by.contains("director") && by.contains("title")) {
            log.debug("Инициализирован поиск фильмов по названию и по режессеру.");
            String SQL_FIND_ANYWAY = String.format(
                    SQL_FIND_BY_PARAMETER, "LOWER(f.name) LIKE LOWER(?) OR LOWER(D.name) LIKE LOWER(?)");
            return namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL_FIND_ANYWAY, this::makeFilm, "%" + query + "%", "%" + query + "%");

        } else if (by.contains("title")) {
            log.debug("Инициализирован поиск фильмов по названию.");
            String SQL = String.format(SQL_FIND_BY_PARAMETER, "LOWER(f.name) LIKE LOWER(?)");
            return namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL, this::makeFilm, "%" + query + "%");

        } else if (by.contains("director")) {
            log.debug("Инициализирован поиск фильмов по режессеру.");
            String SQL_BY_DIRECTOR = String.format(SQL_FIND_BY_PARAMETER, "LOWER(D.name) LIKE LOWER(?)");
            return namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL_BY_DIRECTOR, this::makeFilm, "%" + query + "%");
        }

        return List.of();
    }

    @Override
    public List<Film> findFilmBySorting(Long directorId, String sortBy) {
        List<Film> films = new ArrayList<>();
        if (sortBy.equalsIgnoreCase("year")) {
            log.debug("Инициализирован поиск фильма по году.");
            films = namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL_FIND_BY_YEAR, this::makeFilm, directorId);
        }

        else if (sortBy.equalsIgnoreCase("likes")) {
            log.debug("Инициализирован поиск фильма по лайкам.");
            films = namedParameterJdbcTemplate.getJdbcOperations().query(
                    SQL_FIND_BY_LIKES, this::makeFilm, directorId);
        }

        if (films.isEmpty()) {
            throw new FilmNotFountException("Фильмы по таким параметрам запроса не нашлись (((");
        }

        return films;
    }

    @Override
    public void deleteFilmById(Long id) {
        namedParameterJdbcTemplate.update(SQL_DELETE_FILM_BY_ID, new MapSqlParameterSource());
    }

    /**
     * Метод по добавлению жанров и режиссеров в БД в свои таблицы.
     * @param film фильм из которого извлекаются жанры и режиссеры.
     */
    private void addGenreAndDirector(Film film) {
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
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = mpaRating.getMpaById(resultSet.getInt("mpa_rating"));
        Set<Genre> genre = new HashSet<>(genreDao.getGenreByFilm(resultSet.getLong("film_id")));
        Set<Long> likes = likesDao.findLikesByFilm(resultSet.getLong("film_id"));
        Set<Director> directors = new HashSet<>(filmDirectorDao.getDirectorByFilm(resultSet.getLong("film_id")));

        return Film.builder()
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .id(resultSet.getLong("film_id"))
                .mpa(mpa)
                .genres((genre))
                .likesUserId(likes)
                .directors(directors)
                .build();
    }
}
