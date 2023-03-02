package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class MpaRatingImpl implements MpaRatingDao {

    String SQL_GET_ALL_MPA = "SELECT * FROM mpa_rating ORDER BY MPA_ID ASC ";

    String SQL_GET_MPA_BY_ID = "SELECT * FROM mpa_rating WHERE mpa_id = ?";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(SQL_GET_ALL_MPA, this::makeMpa);
    }

    @Override
    public Mpa getMpaById(int id) {
        Optional<Mpa> mpa = jdbcTemplate.query(SQL_GET_MPA_BY_ID, this::makeMpa, id)
                .stream()
                .findFirst();

        if(mpa.isEmpty()) {
            throw new MpaNotFoundException(String.format("Mpa rating с ID=%d не существует", id));
        }
        return mpa.get();
    }

    private Mpa makeMpa (ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name"));
    }
}
