package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDirectorDaoImpl implements FilmDirectorDao {

    private final static String SQL_ADD_DIRECTOR = "INSERT INTO director(name) VALUES (?)";

    private final static String SQL_UPDATE_DIRECTOR = "UPDATE director SET name = ? WHERE id = ?";

    private final static String SQL_DELETE_DIRECTOR = "DELETE FROM director WHERE id = ?";

    private final static String SQL_FIND_DIRECTOR_BY_ID = "SELECT * FROM director WHERE id = ?";

    private final static String SQL_FIND_ALL_DIRECTORS = "SELECT * FROM director ORDER BY id ASC";

    private final static String SQL_INSERT_FILM_DIRECTOR = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";

    private final static String SQL_DELETE_ROW_FILM_DIRECTOR = "DELETE FROM film_director WHERE film_id = ?";

    private final static String SQL_GET_DIRECTOR_BY_FILM = "SELECT * FROM DIRECTOR d " +
            "INNER JOIN FILM_DIRECTOR FD ON d.ID = FD.DIRECTOR_ID WHERE FILM_ID = ?";

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Director addDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_DIRECTOR, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        log.debug("Добавлен новый режисер=[{}]", director);

        return director;
    }
    @Override
    public void deleteDirector(Long directorId) {
        log.debug("Режессер с ID={} был удален.", directorId);
        jdbcTemplate.update(SQL_DELETE_DIRECTOR, directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        int check = jdbcTemplate.update(SQL_UPDATE_DIRECTOR, director.getName(), director.getId());

        if (check == 0) {
            throw new DirectorNotFoundException(String.format("Режиссер с ID=%d не существует", director.getId()));
        }
        log.debug("Обновлен режессер с ID={}.", director.getId());
        return findDirectorById(director.getId());
    }

    @Override
    public Director findDirectorById(Long directorId) {
        log.debug("Запрошен режессер с ID={}.", directorId);
        return jdbcTemplate.query(SQL_FIND_DIRECTOR_BY_ID, this::makeDirector, directorId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new DirectorNotFoundException(String.format(
                        "Режиссер с ID=%d не найден!", directorId)));
    }

    @Override
    public List<Director> findAllDirectors() {
        log.debug("Запрошен список всех режессеров.");
        return jdbcTemplate.query(SQL_FIND_ALL_DIRECTORS, this::makeDirector);
    }


    @Override
    public void insertToFilmDirector(Long filmId, Long directorId) {
        log.debug("Вставлена запись в таблицу film_director.");
        jdbcTemplate.update(SQL_INSERT_FILM_DIRECTOR, filmId, directorId);
    }

    @Override
    public void deleteRowFromFilmDirector(Long filmId) {
        log.debug("Удалена запись из таблицы film_director.");
        jdbcTemplate.update(SQL_DELETE_ROW_FILM_DIRECTOR, filmId);
    }

    @Override
    public Collection<Director> getDirectorByFilm(Long filmId) {
        return jdbcTemplate.query(SQL_GET_DIRECTOR_BY_FILM, this::makeDirector, filmId);
    }

    private Director makeDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(resultSet.getLong("id"), resultSet.getString("name"));
    }
}
