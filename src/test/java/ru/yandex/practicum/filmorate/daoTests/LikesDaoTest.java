package ru.yandex.practicum.filmorate.daoTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LikesDaoTest {


    private final LikesDao likesDao;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;



    @BeforeEach
    public void setUp() {
        Film film = Film.builder()
                .name("Film Updated")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(190)
                .description("New film update decription")
                .mpa(new Mpa(2, "PG"))
                .genres(new HashSet<>(Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма"), new Genre(3, "Мультфильм"))))
                .build();
        Film film2 = Film.builder()
                .name("New film")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(120)
                .description("New film about friends")
                .mpa(new Mpa(3, "PG-13"))
                .genres(new HashSet<>(Set.of(new Genre(1, "Комедия"))))
                .build();
        User user = User.builder()
                .login("user")
                .name("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(1990, 7, 2))
                .build();

        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film2);
        userDbStorage.addUser(user);
    }

    @Test
    public void shouldAddLikeToFilmFromUser() {
        likesDao.putLike(2L, 1L);

        assertThat(filmDbStorage.findFilmById(2L).getLikesUserId().size())
                .as("Лайк не добавился в таблице like_to_film").isEqualTo(1);
    }


    @Test
    public void shouldRemoveLikeToFilmFromUser() {
        likesDao.putLike(2L, 1L);
        System.out.println(filmDbStorage.findFilmById(2L));

        likesDao.removeLike(2L, 1L);
        System.out.println(filmDbStorage.findFilmById(2L));

        assertThat(filmDbStorage.findFilmById(2L).getLikesUserId().isEmpty())
                .as("Лайк не удалился из таблицы like_to_film").isTrue();
    }

    @Test
    public void shouldReturnPopularFilm() {
        likesDao.putLike(2L, 1L);

        List<Film> films = likesDao.getPopularFilm(2);

        assertThat(films.size()).as("Возвращается не весь список фильмов.").isEqualTo(2);
        assertThat(films.stream().mapToLong(Film::getId).toArray())
                .as("Фильмы возвращаются не отсортированными по кол-ву лайков.")
                .isEqualTo(new long[]{2, 1});

    }
}
