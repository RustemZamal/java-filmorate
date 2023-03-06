package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Component
public class LikesDaoImpl implements LikesDao {

    private final static String SQL_ADD_LIKE = "INSERT INTO like_to_film (user_id, film_id) VALUES (?, ?)";

    private final static String SQL_REMOVE_LIKE = "DELETE FROM like_to_film WHERE user_id = ? AND film_id = ?";

    private final static String SQL_GET_LIKES = "SELECT USER_ID FROM like_to_film WHERE FILM_ID = ?";


    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putLike(Long filmId, Long userId) {
        log.debug("Пользователь с id={} поставил лайк фильму с id={}", userId, filmId);
        jdbcTemplate.update(SQL_ADD_LIKE, userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.debug("Пользователь с id={} удалил лайк фильму с id={}", userId, filmId);
        jdbcTemplate.update(SQL_REMOVE_LIKE, userId, filmId);
    }

    @Override
    public Set<Long> findLikesByFilm(Long filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_LIKES, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }


}
