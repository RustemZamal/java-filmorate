package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.PostNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long generatedId = 1;

    private final Map<Long, Film> films = new HashMap<>();


    @Override
    public Film addFilm(Film film) {
        film.setId(generatedId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new PostNotFoundException(
                    String.format("Не возможно обновить данные о фильм с несуществующим id - %d", film.getId()));
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new PostNotFoundException(String.format("Фильм с id-%d не найден", id));
        }
        return films.get(id);
    }

    @Override
    public List<Film> getPopularFilm(Integer count) {
        return null;
    }

    @Override
    public List<Film> findByParameter(String query, String by) {
        return null;
    }

    @Override
    public List<Film> findFilmBySorting(Long directorId, String sortBy) {
        return null;
    }

    @Override
    public void deleteFilmById(Long id) {
        films.remove(id);
    }
}
