package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;


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
        return userStorage.findUserById(id).getFriends()
                .stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    /**
     * Метод по нахождения общих друзей.
     * @param id идентификатор пользователя.
     * @param otherId идентификатор другого пользователя.
     * @return Возвращает список общих друзей пользователя с id и otherId.
     */
    public List<User> findCommonFriends(Long id, Long otherId) {
        Set<Long> ids = userStorage.findUserById(id).getFriends();
        Set<Long> otherIds = userStorage.findUserById(otherId).getFriends();

        return ids.stream()
                .filter(otherIds::contains)
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    /**
     * Метод по добавлению в друзья.
     * @param id идентификатор пользователя который добавляет в друзья.
     * @param friendId идентификатор того, кого добавляют в друзья.
     */
    public void addToFriends(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    /**
     * Метод по удалению из друзей.
     * @param id идентификатор пользователя.
     * @param friendId идентификатор пользователя, который является другом.
     */
    public void deleteFromFriends(Long id, long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

}
