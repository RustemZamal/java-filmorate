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

    private final Map<Integer, User> users = new LinkedHashMap<>();

    private int generatedId = 1;

    @PostMapping
    @ResponseBody
    public User createUser(@Valid @RequestBody User user) {
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
        if (!users.containsKey(user.getId())) {
            log.debug("Не возможно обновить данные не существующего пользователя с id - {}", user.getId());
            throw new ValidationException("Не возможно обновить данные пользователя с несуществующем id");
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
}
