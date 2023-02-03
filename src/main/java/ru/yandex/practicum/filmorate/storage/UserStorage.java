package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    /**
     * Метод по созданию пользователя.
     * @param user фильм, который необходимо создать.
     * @return Возвращает созданный объект.
     */
    User addUser(User user);

    /**
     * Метод по модифицированию данных пользователя.
     * @param user фильм, который необходимо изменить.
     * @return Возвращает измененного пользователя.
     */
    User updateUser(User user);

    /**
     * Метод по нахождению пользователя по id.
     * @param userId идентификатор пользователя.
     * @return Возвращает пользователя по его id.
     */
    User findUserById(Long userId);

    /**
     * Метод по нахождению всех пользователей.
     * @return Возвращает список всех пользователей.
     */
    List<User> getAllUsers();

    void deleteUser(Long id);

    void deleteAllUsers();
}
