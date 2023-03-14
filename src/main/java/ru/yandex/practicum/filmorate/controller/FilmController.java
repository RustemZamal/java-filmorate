package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

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
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
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
    public Film getFilmById(@PathVariable() Long id) {
        return filmService.findFilmById(id);
    }

    /**
     * Эндпоинт по нахождению популярных по количеству лайков фильмов.
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список фильмов согласна параметру count. {@link FilmService#findPopularFilms(Integer)}
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam (defaultValue = "-1") Integer genreId,
                                      @RequestParam (defaultValue = "-1") Integer year) {
        if (genreId != -1 || year != -1) {
            return  filmService.findPopularByDateAndGenre(count, genreId, year);
        } else {
            return filmService.findPopularFilms(count);
        }
    }

    @GetMapping("/search")
    public List<Film> findByParameter(@RequestParam String query, @RequestParam String by) {
        return filmService.findByParameter(query, by); // query
    }


    @GetMapping("/director/{directorId}")
    public List<Director> findFilmBySorting(@PathVariable Long directorId, @RequestParam String sortBy) {
        return null;
    }

    /**
     * Эндпоинт по удалению лайка фильму пользователем. {@link FilmService#deleteLike(Long, Long)}
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {

        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteFilmById() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "метод удаления по пути /films/id еще не реализован!");

    }

}

