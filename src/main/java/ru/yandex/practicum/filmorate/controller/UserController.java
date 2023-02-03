package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Эндпоин для создания пользователя {@link UserService#createUser(User)}
     * @param user фильм, который необходимо создать
     * @return В ответ возвращает созданный объект
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Эндпоинт по модифицированию данных пользователя.
     * @param user фильм, который необходимо изменить.
     * @return В ответ возвращает измененного пользователя. {@link UserService#updateUser(User)}
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Эндпоинт по добавлению в друзья. {@link UserService#addToFriends(Long, Long)}
     * @param id идентификатор пользователя который добавляет в друзья.
     * @param friendId идентификатор того, кого добавляют в друзья.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable(required = false) Long id,
            @PathVariable(required = false) Long friendId) {

        userService.addToFriends(id, friendId);
    }

    /**
     * Эндпоинт по нахождению всех пользователей.
     * @return Возвращает список всех пользователей. {@link UserService#getAllUsers()}
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Эндпоинт по нахождению пользователя по id.
     * @param id идентификатор пользователя.
     * @return Возвращает пользователя по его id. {@link UserService#findUserById(Long)}
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable(required = false) Long id) {
        return userService.findUserById(id);
    }

    /**
     * Эндпоинт по нахождению всех друзей определенного пользователя.
     * @param id идентификатор пользователя.
     * @return Возвращает список друзей пользователя. {@link UserService#findFriends(Long)}
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable(required = false) Long id) {
        return userService.findFriends(id);
    }

    /**
     * Эндпоинт по нахождения общих друзей.
     * @param id идентификатор пользователя.
     * @param otherId идентификатор другого пользователя.
     * @return Возвращает список общих друзей пользователя с id и otherId. {@link UserService#findCommonFriends(Long, Long)}
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable(required = false) Long id,
            @PathVariable(required = false) Long otherId) {

        return userService.findCommonFriends(id, otherId);
    }

    /**
     * Эндпоинт по удалению из друзей. {@link UserService#deleteFromFriends(Long, long)}
     * @param id идентификатор пользователя.
     * @param friendId идентификатор пользователя, который является другом.
     */
    @DeleteMapping(value = {"/friends", "/{id}/friends/{friendId}"})
    public void deleteFromFriends(@PathVariable(required = false) Long id,
                                  @PathVariable(required = false) Long friendId) {

            userService.deleteFromFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteUser(@PathVariable(required = false) Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "метод /users/{id} еще не реализован");
    }
}
