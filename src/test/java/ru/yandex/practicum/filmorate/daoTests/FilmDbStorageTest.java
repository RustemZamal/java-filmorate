package ru.yandex.practicum.filmorate.daoTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFountException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    private final LikesDao likesDao;

    private final UserDbStorage userDbStorage;


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
        User user = User.builder()
                .login("user")
                .name("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(1990, 7, 2))
                .build();


        filmStorage.addFilm(film);
        filmStorage.addFilm(film2);
        userDbStorage.addUser(user);

    }

    @Test
    public void shouldFindFilmById() {
        Film film = filmStorage.findFilmById(1L);

        assertThat(film)
                .as("?????????? ???? ?????? ????????????????.")
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void shouldFindAllFilms() {
        assertThat(filmStorage.findAllFilms())
                .as("???????????????????? ?????????????? ???????????? ???????? 2")
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
                .genres(new HashSet<>(Set.of(new Genre(1, "??????????????"), new Genre(2, "??????????"), new Genre(3, "????????????????????"))))
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
    public void shouldReturnPopularFilm() {
        likesDao.putLike(2L, 1L);

        List<Film> films = filmStorage.getPopularFilm(2);

        assertThat(films.size()).as("???????????????????????? ???? ???????? ???????????? ??????????????.").isEqualTo(2);
        assertThat(films.stream().mapToLong(Film::getId).toArray())
                .as("???????????? ???????????????????????? ???? ???????????????????????????????? ???? ??????-???? ????????????.")
                .isEqualTo(new long[]{2, 1});

    }

    @Test
    public void shouldThrowFilmNotFountException() {
        assertThatExceptionOfType(FilmNotFountException.class)
                .as("???????????????????? FilmNotFountException ???? ?????????????????????????? ")
                .isThrownBy(() -> filmStorage.findFilmById(999L));

    }
}