package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private int generatedId = 1;

    @PostMapping
    @ResponseBody
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(generatedId++);
        films.put(film.getId(), film);
        log.debug("Был добавлен фильм, текущее кол-во фильмов: {}", films.size());

        return film;
    }

    @PutMapping
    @ResponseBody
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Не возможно обновить данные о фильме с несуществующем id - {}", film.getId());
            throw new ValidationException("Не возможно обновить данные о фильм с несуществующим id");
        }

        films.put(film.getId(), film);
        log.debug("Данные о фильме c id - {} обновились", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public void deleteAllFilms() {
        films.clear();
    }
}

