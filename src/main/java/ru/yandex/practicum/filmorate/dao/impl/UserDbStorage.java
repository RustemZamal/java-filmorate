package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.UserMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {

    String SQL_INSERT_INTO_USERS = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (:email, :login, :name, :birthday)";

    String SQL_UPDATE_USER = "UPDATE users SET email = :email," +
            " login = :login, name = :name, birthday = :birthday WHERE user_id = :user_id";

    String SQL_FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = :user_id";

    String SQL_GET_ALL_USERS = "SELECT * FROM USERS";

    String SQL_DELETE_USER_BY_ID = "DELETE FROM users WHERE user_id = :user_id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    private final FriendDao friendDao;

    @Override
    public User addUser(User user) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        try {
            namedParameterJdbcTemplate.update(SQL_INSERT_INTO_USERS, parameters, holder);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("EMAIL"))
                throw new UserAlreadyExistException("This EMAIl already exist");
            else
                throw new UserAlreadyExistException("This LOGIN already exist");
        }

        user.setId(holder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        findUserById(user.getId());
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        namedParameterJdbcTemplate.update(SQL_UPDATE_USER, parameters);

        return findUserById(user.getId());
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = namedParameterJdbcTemplate.query(
                SQL_FIND_USER_BY_ID, new MapSqlParameterSource("user_id", userId), new UserMapper())
                .stream()
                .findFirst();

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("There is no user with ID = %d", userId));
        }

        return user.get();
    }

    @Override
    public List<User> getAllUsers() {
         return namedParameterJdbcTemplate.query(SQL_GET_ALL_USERS, new UserMapper());
    }

    @Override
    public void deleteUserById(Long id) {
        namedParameterJdbcTemplate.update(SQL_DELETE_USER_BY_ID, new MapSqlParameterSource("user_id", id));
    }

    @Override
    public void deleteAllUsers() {

    }

}
