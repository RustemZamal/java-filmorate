package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class LikesDaoImpl implements LikesDao {

    String SQL_ADD_LIKE = "INSERT INTO like_to_film (user_id, film_id) VALUES (?, ?)";

    String SQL_REMOVE_LIKE = "DELETE FROM like_to_film WHERE user_id = ? AND film_id = ?";

    String SQL = "SELECT fm.* " +
            "FROM film AS fm " +
            "LEFT JOIN like_to_film AS lf ON fm.film_id = lf.film_id " +
            "GROUP BY fm.film_id " +
            "ORDER BY COUNT(lf.user_id) DESC " +
            "LIMIT ?";

    private final MpaRatingDao mpaRating;

    private final GenreDao genreDao;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_REMOVE_LIKE, userId, filmId);
    }

    @Override
    public List<Film> getPopularFilm(Integer count) {
        return jdbcTemplate.query(SQL, this::makeMostPopularFilm, count);
    }

    private Film makeMostPopularFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = mpaRating.getMpaById(resultSet.getInt("mpa_rating"));
        Set<Genre> genre = new LinkedHashSet<>(genreDao.getGenreByFilm(resultSet.getLong("film_id")));

       return Film.builder()
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .id(resultSet.getLong("film_id"))
                .mpa(mpa)
                .genres((genre))
                .build();

    }


}
