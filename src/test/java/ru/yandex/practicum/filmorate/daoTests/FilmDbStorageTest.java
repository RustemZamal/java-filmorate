package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFountException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;


    @BeforeEach
    public void setUpTest() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .description("adipisicing")
                .mpa(new Mpa(1, null))
                .build();
        Film film2 = Film.builder()
                .name("New film")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .description("New film about friends")
                .mpa(new Mpa(3, null))
                .genres(new HashSet<>(Set.of(new Genre(1, null))))
                .build();

        filmStorage.addFilm(film);
        filmStorage.addFilm(film2);
    }

    @Test
    public void shouldFindFilmById() {
        Film film = filmStorage.findFilmById(1L);

        assertThat(film)
                .as("Фильм не был добавлен.")
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void shouldFindAllFilms() {
        assertThat(filmStorage.findAllFilms())
                .as("Количество фильмов должно быть 2")
                .hasSize(2);

    }

    @Test
    public void shouldUpdateFilm() {
        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Film Updated")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(190)
                .description("New film update decription")
                .mpa(new Mpa(2, "PG"))
                .genres(new HashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма"), new Genre(3, "Мультфильм"))))
                .build();

        Film actualFilm = filmStorage.updateFilm(updatedFilm);

        assertThat(actualFilm.getName()).as("Fail name").isEqualTo(updatedFilm.getName());
        assertThat(actualFilm.getReleaseDate()).as("Fail releaseDate").isEqualTo(updatedFilm.getReleaseDate());
        assertThat(actualFilm.getDuration()).as("Fail duration").isEqualTo(updatedFilm.getDuration());
        assertThat(actualFilm.getDescription()).as("Fail description").isEqualTo(updatedFilm.getDescription());
        assertThat(actualFilm.getMpa()).as("Fail mpa").isEqualTo(updatedFilm.getMpa());
        assertThat(actualFilm.getGenres()).as("Fail mpa").isEqualTo(updatedFilm.getGenres());
    }

    @Test
    public void shouldThrowFilmNotFountException() {
        assertThatExceptionOfType(FilmNotFountException.class)
                .as("Исключение FilmNotFountException не выбрасывается ")
                .isThrownBy(() -> filmStorage.findFilmById(999L));

    }
}