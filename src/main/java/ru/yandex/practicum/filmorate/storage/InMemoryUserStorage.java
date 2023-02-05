package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage{

    private long generatedId = 1;

    private final Map<Long, User> users = new HashMap<>();


    private long generateId() {
        return generatedId++;
    }

    @Override
    public User addUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException(String.format("Пользователь с почтой %s уже существует", user.getEmail()));
        }

        user.setId(generatedId++);
        users.put(user.getId(), user);
        return user;
    }


    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(
                    String.format("Не возможно обновить данные пользователя с несуществующем id - %d", user.getId()));
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(
                    String.format("Пользователь с id - %d не найден", userId));
        }
        return users.get(userId);
    }
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
