package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    /**
     * Метод по добавлению фильма
     * @param film фильм который необходимо создать
     * @return Возвращает созданный фильм
     */
    Film addFilm(Film film);

    /**
     * Метод по модифицированию фильма
     * @param film фильм, который необходимо изменить
     * @return возвращает измененный фильм
     */
    Film updateFilm(Film film);

    /**
     * Метод по возвращению всех фильмов.
     * @return Возвращает все фильмы.
     */
    List<Film> findAllFilms();

    /**
     * Метод по нахождению фильма по его id.
     * @param id идентификатор фильма.
     * @return Возвращает фильм по его id.
     */
    Film findFilmById(Long id);

    void deleteFilmById(Long id);


    void deleteAllFilms();
}
