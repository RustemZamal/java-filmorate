package ru.yandex.practicum.filmorate.daoTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;


    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        User friend = User.builder()
                .login("friend")
                .name("friend adipisicing")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1976, 8, 20))
                .build();
        User commonFriend = User.builder()
                .login("common")
                .email("friend@common.ru")
                .birthday(LocalDate.of(2000, 8, 20))
                .build();

        userDbStorage.addUser(user);
        userDbStorage.addUser(friend);
        userDbStorage.addUser(commonFriend);
    }

    @Test
    public void shouldUpdateUser() {
        User updatedUser = User.builder()
                .id(1L)
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976, 9, 20))
                .build();

        User actualUser = userDbStorage.updateUser(updatedUser);

        assertThat(actualUser)
                .as("Fail login.").hasFieldOrPropertyWithValue("login", updatedUser.getLogin())
                .as("Fail name.").hasFieldOrPropertyWithValue("name", updatedUser.getName())
                .as("Fail email.").hasFieldOrPropertyWithValue("email",updatedUser.getEmail())
                .as("Fail birthday.").hasFieldOrPropertyWithValue("birthday",updatedUser.getBirthday());
    }

    @Test
    public void shouldReturnUserById() {
        User actualUser = userDbStorage.findUserById(1L);

        assertThat(actualUser).as("Пользователь не возвращается.").isNotNull();
        assertThat(actualUser)
                .as("Возвращается не верный пользователь.")
                .hasFieldOrPropertyWithValue("login", "dolore");
    }

    @Test
    public void shouldReturnAllUsers() {
        List<User> actualUsers = userDbStorage.getAllUsers();
        assertThat(actualUsers.size())
                .as("Возвращается не верное кол-во пользователей.")
                .isEqualTo(3);
    }

    @Test
    public void shouldThrowUserNotFoundException() {
        assertThatExceptionOfType(UserNotFoundException.class)
                .as("Исключение UserNotFoundException не выбрасывается")
                .isThrownBy(() -> userDbStorage.findUserById(999L));
    }
}
