package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Эндпоин для создания фильма {@link ru.yandex.practicum.filmorate.storage.FilmStorage#addFilm(Film)}
     * @param film фильм, который необходимо создать
     * @return В ответ возвращает созданный объект
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {

        return filmService.addFilm(film);
    }

    /**
     * Эндпоинт по модифицированию фильма.
     * @param film фильм, который необходимо изменить.
     * @return В ответ возвращает измененный фильм. {@link FilmService#updateFilm(Film)}
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * Эндпоинт позволяющий пользователем ставить лайки фильмам. {@link FilmService#putLike(Long, Long)}
     * @param id фильм, которому ставиться лайк.
     * @param userId пользователь, который ставит лайк.
     */
    @PutMapping("/{id}/like/{userId}")
    public void putLike(
            @PathVariable(required = false) Long id,
            @PathVariable(required = false) Long userId) {

        filmService.putLike(id, userId);
    }

    /**
     * Эндпоинт по возвращению всех фильмов.
     * @return Возвращает все фильмы. {@link FilmService#findAllFilms()}
     */
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.findAllFilms();
    }

    /**
     * Эндпоинт по нахождению фильма по его id.
     * @param id идентификатор фильма.
     * @return Возвращает фильм по его id. {@link FilmService#findFilmById(Long)}
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(required = false) Long id) {
        return filmService.findFilmById(id);
    }

    /**
     * Эндпоинт по нахождению популярных по количеству лайков фильмов.
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список фильмов согласна параметру count. {@link FilmService#findPopularFilms(Integer)}
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.findPopularFilms(count);
    }

    /**
     * Эндпоинт по удалению лайка фильму пользователем. {@link FilmService#deleteLike(Long, Long)}
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable(required = false) Long id,
            @PathVariable(required = false) Long userId) {

        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteFilm(@PathVariable(required = false) Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "метод /films/{id} еще не реализован");
    }
}

