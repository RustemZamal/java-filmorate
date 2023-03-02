package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.UserMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FriendDaoImpl implements FriendDao {

    String SQL_FIND_FRIENDS = "SELECT U.* " +
            "FROM USERS AS U " +
            "INNER JOIN FRIENDSHIP F ON U.USER_ID = F.FRIEND_ID " +
            "WHERE F.USER_ID = ? " +
            "UNION " +
            "SELECT U.* " +
            "FROM USERS AS U " +
            "INNER JOIN FRIENDSHIP F on U.USER_ID = F.USER_ID " +
            "WHERE F.FRIEND_ID = ? AND STATUS = True";

    String SQL_FIND_COMMON_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN (SELECT CASE " +
            "WHEN (user_id = ? AND friend_id != ?) THEN friend_id " +
            "WHEN (user_id != ? AND friend_id = ?) THEN user_id " +
            "END " +
            "FROM FRIENDSHIP " +
            "INTERSECT " +
            "SELECT CASE " +
            "WHEN (user_id = ? AND friend_id != ?) THEN friend_id " +
            "WHEN (user_id != ? AND friend_id = ?) THEN user_id " +
            "END " +
            "FROM FRIENDSHIP);";

    String SQL_IS_USERS_FRIENDS = "SELECT * FROM FRIENDSHIP " +
            "WHERE USER_ID = ? AND FRIEND_ID = ? " +
            "OR FRIEND_ID = ? AND USER_ID = ?";

    String SQL_INSERT_INTO_FRIENDSHIP = "INSERT INTO FRIENDSHIP (user_id, friend_id) VALUES (?, ?)";

    String SQL_UPDATE_FRIENDSHIP = "UPDATE FRIENDSHIP SET STATUS = ? " +
            "WHERE USER_ID = ? AND FRIEND_ID = ? " +
            "OR FRIEND_ID = ? AND USER_ID = ?";

    String SQL_DELETE_FROM_FRIENDS = "DELETE FROM friendship " +
            "WHERE user_id = ? AND friend_id = ?";

    String SQL_USER_EXIST = "SELECT * FROM users WHERE user_id = ?";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAllFriends(Long id) {
        User user = userExist(id);

        return jdbcTemplate.query(SQL_FIND_FRIENDS, new UserMapper(), user.getId(), user.getId());
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        return jdbcTemplate.query(
                SQL_FIND_COMMON_FRIENDS,
                new UserMapper(),
                id, otherId, otherId, id, otherId, id, id, otherId);
    }

    @Override
    public void addToFriends(Long id, Long friendId) {
        Optional<Friendship> friendship = jdbcTemplate.query(
                        SQL_IS_USERS_FRIENDS, this::makeFriendship, id, friendId, id, friendId)
                .stream()
                .findFirst();

        if (friendship.isEmpty()) {
            jdbcTemplate.update(SQL_INSERT_INTO_FRIENDSHIP, id, friendId);
        } else if (!friendship.get().isStatus() && friendship.get().getUserId().equals(id)
                && friendship.get().getFriendId().equals(friendId)) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователи с ID-%d уже добавил пользователя с ID-%d в друзья", id, friendId));
        } else if (friendship.get().getFriendId().equals(id) && friendship.get().getUserId().equals(friendId)
                && (!friendship.get().isStatus())) {
            jdbcTemplate.update(SQL_UPDATE_FRIENDSHIP, true, id, friendId, id, friendId);
        } else {
            throw new UserAlreadyExistException(String.format(
                    "Пользователи с ID-%d уже потвердил дружбу с пользователем с ID-%d", id, friendId));
        }
    }

    @Override
    public void deleteFromFriends(Long id, long friendId) {
        jdbcTemplate.update(SQL_DELETE_FROM_FRIENDS, id, friendId);
    }

    private Friendship makeFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return new Friendship(
                resultSet.getLong("user_id"),
                resultSet.getLong("friend_id"),
                resultSet.getBoolean("status"));
    }

    private User userExist(Long userId) {
        Optional<User> user = jdbcTemplate.query(SQL_USER_EXIST, new UserMapper(), userId)
                .stream()
                .findFirst();

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("There is no user with ID = %d", userId));
        }

        return user.get();
    }


}
