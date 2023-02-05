package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    /**
     * Метод по добавлению фильма
     * @param film фильм который необходимо создать {@link FilmStorage#addFilm(Film)}
     * @return Возвращает фильм
     */
    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    /**
     * Метод по модифицированию фильма {@link FilmStorage#updateFilm(Film)}
     * @param film фильм, который необходимо изменить
     * @return Возвращает измененный фильм
     */
    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    /**
     * Метод позволяющий пользователем ставить лайки фильмам.
     * @param id фильм, которому ставиться лайк.
     * @param userId пользователь, который ставит лайк.
     */
    public void putLike(Long id, Long userId) {
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id).getLikesUserId().add(userId);
    }

    /**
     * Метод по возвращению всех фильмов.
     * @return Возвращает все фильмы. {@link FilmStorage#findAllFilms()}
     */
    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    /**
     * Метод по нахождению фильма по его id.
     * @param id идентификатор фильма.
     * @return Возвращает фильм по его id. {@link FilmStorage#findFilmById(Long)}
     */
    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    /**
     * Метод по нахождению популярных по количеству лайков фильмов.
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список популярных фильмов согласна параметру count.
     */
    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.findAllFilms()
                .stream()
                .sorted((f1, f2) -> -(f1.getLikesUserId().size() - f2.getLikesUserId().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Метод по удалению лайка фильму пользователем.
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    public void deleteLike(Long id, Long userId) {
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id).getLikesUserId().remove(userId);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }

}
