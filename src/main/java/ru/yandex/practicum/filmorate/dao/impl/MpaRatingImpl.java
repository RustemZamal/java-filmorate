package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MpaRatingImpl implements MpaRatingDao {

    private final static String SQL_GET_ALL_MPA = "SELECT * FROM mpa_rating ORDER BY MPA_ID ASC ";

    private final static String SQL_GET_MPA_BY_ID = "SELECT * FROM mpa_rating WHERE mpa_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        log.debug("Запрошен список всех Mpa ретингов.");
        return jdbcTemplate.query(SQL_GET_ALL_MPA, this::makeMpa);
    }

    @Override
    public Mpa getMpaById(int id) {
         log.debug("Запрошен Mpa рейтинг с id={}.", id);
        return jdbcTemplate.query(SQL_GET_MPA_BY_ID, this::makeMpa, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new MpaNotFoundException(String.format("Mpa rating с ID=%d не существует", id)));
    }

    private Mpa makeMpa (ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name"));
    }
}
