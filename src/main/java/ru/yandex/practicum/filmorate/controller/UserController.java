package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    protected final Map<Integer, User> users = new LinkedHashMap<>();

    protected int generatedId = 1;

    @PostMapping
    @ResponseBody
    public User createUser(@Valid @RequestBody User user) {
        checkSpaces(user);

        user.setId(generatedId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Был добавлен пользователь, текущее кол-во пользователей: {}", users.size());
        return user;
    }

    @PutMapping
    @ResponseBody
    public User updateUser(@Valid @RequestBody User user) {
        checkSpaces(user);

        if (!users.containsKey(user.getId())) {
            log.debug("Не возможно обновить данные не существующего пользователя с id - {}", user.getId());
            throw new ValidationException("Не возможно обновить данные не существующего пользователя");
        }

        users.put(user.getId(), user);
        log.debug("Данные пользователя были изменены");
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUsers() {
        users.clear();
    }

    private void checkSpaces(User user) {
        if (user.getLogin().contains(" ")) {
            log.debug("Логин содержит пробелы");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }
}
