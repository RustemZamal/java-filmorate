package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesDao likesDao;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage, LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesDao = likesDao;
    }


    /**
     * Метод по добавлению фильма
     * @param film фильм который необходимо создать {@link FilmStorage#addFilm(Film)}
     * @return Возвращает фильм
     */
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    /**
     * Метод по модифицированию фильма {@link FilmStorage#updateFilm(Film)}
     * @param film фильм, который необходимо изменить
     * @return Возвращает измененный фильм
     */
    public Film updateFilm(Film film) {
        if (film.getGenres() != null) {
            film.setGenres(film.getGenres()
                    .stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        return filmStorage.updateFilm(film);
    }

    /**
     * Метод позволяющий пользователем ставить лайки фильмам.
     * @param id фильм, которому ставиться лайк.
     * @param userId пользователь, который ставит лайк.
     */
    public void putLike(Long id, Long userId) {
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id);

        likesDao.putLike(id, userId);
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
        return filmStorage.getPopularFilm(count);
    }

    /**
     * Метод по нахождению популярных по количеству лайков фильмов, году и жанру.
     * @param count количество фильмов, по умолчанию 10.
     * @genreId genreId id жанра.
     * @year year год выхода фильма.
     * @return Возвращает список популярных фильмов согласна параметру count.
     */
    public List<Film> findPopularByDateAndGenre (Integer count, Integer genreId, Integer year){
        return filmStorage.getPopularFilmByDateAndGenre(count, genreId, year);
    }

    /**
     * Метод по удалению лайка фильму пользователем.
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    public void deleteLike(Long id, Long userId) {
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id);

        likesDao.removeLike(id, userId);
    }

    public List<Film> findByParameter(String query, String by) {
        return filmStorage.findByParameter(query, by);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }

}
