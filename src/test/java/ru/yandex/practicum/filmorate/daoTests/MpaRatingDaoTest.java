package ru.yandex.practicum.filmorate.daoTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRatingDaoTest {

    private final MpaRatingDao mpaRatingDao;

    @Test
    public void shouldGetMpaById() {
        Mpa mpa = mpaRatingDao.getMpaById(1);

        assertThat(mpa)
                .as("Возвращается не тот mpa рейтинг.")
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void shouldReturnAllGenres() {
        List<Mpa> mpas = mpaRatingDao.getAllMpa();

        assertThat(mpas.size())
                .as("Возвращается не полный список mpa рейтингов.")
                .isEqualTo(5);
    }

    @Test
    public void shoutThrowMpaNotFoundException() {
        assertThatExceptionOfType(MpaNotFoundException.class).
                as("Исключение MpaNotFoundException не выбрасывается.")
                .isThrownBy(() -> mpaRatingDao.getMpaById(999));
    }
}
