package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFountException extends RuntimeException {

    public FilmNotFountException(String message) {
        super(message);
    }
}
