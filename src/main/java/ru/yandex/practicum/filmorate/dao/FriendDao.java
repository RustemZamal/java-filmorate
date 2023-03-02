package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendDao {

    /**
     *
     * @param id id пользователя.
     * @return возвращает всех друзей пользователя.
     */
    List<User> findAllFriends(Long id);

    /**
     *
     * Метод по нахождения общих друзей.
     * @param id идентификатор пользователя.
     * @param otherId идентификатор другого пользователя.
     * @return Возвращает список общих друзей пользователя с индентификатором id и otherId.
     */
    List<User> findCommonFriends(Long id, Long otherId);

    /**
     * Метод по добавлению в друзья.
     * @param id идентификатор пользователя который добавляет в друзья.
     * @param friendId идентификатор того, кого добавляют в друзья.
     */
    void addToFriends(Long id, Long friendId);

    /**
     * Метод по удалению из друзей.
     * @param id идентификатор пользователя.
     * @param friendId идентификатор пользователя, который является другом.
     */
    void deleteFromFriends(Long id, long friendId);
}
