package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Service
public class UserService {

    private final UserStorage userStorage;

    private final FriendDao friendDao;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDao friendDao) {
        this.userStorage = userStorage;
        this.friendDao = friendDao;
    }

    /**
     * Метод по созданию пользователя. {@link UserStorage#addUser(User)}
     * @param user фильм, который необходимо создать.
     * @return Возвращает созданный объект.
     */
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.addUser(user);
    }

    /**
     * Метод по модифицированию данных пользователя.
     * @param user фильм, который необходимо изменить.
     * @return Возвращает измененного пользователя. {@link UserStorage#updateUser(User)}
     */
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    /**
     * Метод по нахождению всех пользователей.
     * @return Возвращает список всех пользователей. {@link UserStorage#getAllUsers()}
     */
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    /**
     * Метод по нахождению пользователя по id.
     * @param userId идентификатор пользователя.
     * @return Возвращает пользователя по его id. {@link UserStorage#findUserById(Long)}
     */
    public User findUserById(Long userId) {
        return userStorage.findUserById(userId);
    }

    /**
     * Метод по нахождению всех друзей определенного пользователя.
     * @param id идентификатор пользователя.
     * @return возвращает список друзей пользователя.
     */
    public List<User> findFriends(Long id) {
        userStorage.findUserById(id);
        return friendDao.findAllFriends(id);
    }

    /**
     * Метод по нахождения общих друзей.
     * @param id идентификатор пользователя.
     * @param otherId идентификатор другого пользователя.
     * @return Возвращает список общих друзей пользователя с id и otherId.
     */
    public List<User> findCommonFriends(Long id, Long otherId) {
        userStorage.findUserById(id);
        userStorage.findUserById(otherId);

        return friendDao.findCommonFriends(id, otherId);
    }

    /**
     * Метод по добавлению в друзья.
     * @param id идентификатор пользователя который добавляет в друзья.
     * @param friendId идентификатор того, кого добавляют в друзья.
     */
    public void addToFriends(Long id, Long friendId) {
        userStorage.findUserById(id);
        userStorage.findUserById(friendId);

        friendDao.addToFriends(id, friendId);
    }

    /**
     * Метод по удалению из друзей.
     * @param id идентификатор пользователя.
     * @param friendId идентификатор пользователя, который является другом.
     */
    public void deleteFromFriends(Long id, long friendId) {
        userStorage.findUserById(id);
        userStorage.findUserById(friendId);

        friendDao.deleteFromFriends(id, friendId);
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

}
