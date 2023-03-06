package ru.yandex.practicum.filmorate.utility;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDate date;
        if (rs.getDate("birthday") == null)
            date = null;
        else
            date = rs.getDate("birthday").toLocalDate();

        return User.builder()
                .id(rs.getLong("user_id"))
                .birthday(date)
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build();
    }
}
