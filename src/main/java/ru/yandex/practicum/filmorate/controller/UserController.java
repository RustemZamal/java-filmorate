package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


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
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
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
    public User getUser(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    /**
     * Эндпоинт по нахождению всех друзей определенного пользователя.
     * @param id идентификатор пользователя.
     * @return Возвращает список друзей пользователя. {@link UserService#findFriends(Long)}
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.findFriends(id);
    }

    /**
     * Эндпоинт по нахождения общих друзей.
     * @param id идентификатор пользователя.
     * @param otherId идентификатор другого пользователя.
     * @return Возвращает список общих друзей пользователя с id и otherId. {@link UserService#findCommonFriends(Long, Long)}
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    /**
     * Эндпоинт по удалению из друзей. {@link UserService#deleteFromFriends(Long, long)}
     * @param id идентификатор пользователя.
     * @param friendId идентификатор пользователя, который является другом.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
            userService.deleteFromFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteUserById() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "метод удаления по пути /users/id еще не реализован");
    }
}
