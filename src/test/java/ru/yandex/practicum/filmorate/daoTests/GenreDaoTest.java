package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTest {

    private final GenreDao genreDao;


    @Test
    public void shouldGetGenreById() {
        Genre genre = genreDao.getGenreById(1);

        assertThat(genre)
                .as("Возвращается не тот жанр.")
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void shouldReturnAllGenres() {
        List<Genre> genres = genreDao.getAllGenre();

        assertThat(genres.size())
                .as("Возвращается не полный список жанров")
                .isEqualTo(6);
    }

    @Test
    public void shoutThrowGenreNotFoundException() {
        assertThatExceptionOfType(GenreNotFoundException.class).
                as("Исключение GenreNotFoundException не выбрасывается.")
                .isThrownBy(() -> genreDao.getGenreById(999));
    }
}
