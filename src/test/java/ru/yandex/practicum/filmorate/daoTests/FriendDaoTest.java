package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FriendDaoTest {

    private final FriendDao friendDao;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    private void setUp() {
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
    public void shouldAddToFriends() {
        friendDao.addToFriends(1L, 2L);

        User friend = friendDao.findAllFriends(1L).get(0);

        assertThat(friend)
                .as("Добавился не верный пользователь в качестве друга.")
                .hasFieldOrPropertyWithValue("login", "friend");
    }

    @Test
    public void shouldFindAllFriendsOfUser() {
        friendDao.addToFriends(1L, 2L);
        friendDao.addToFriends(1L, 3L);

        List<User> actualFriends = friendDao.findAllFriends(1L);
        User friend = friendDao.findAllFriends(1L).get(0);
        User friend2 = friendDao.findAllFriends(1L).get(1);

        assertThat(actualFriends.size())
                .as("Возвращается не верное кол-во друзей пользователя.")
                .isEqualTo(2);
        assertThat(friend)
                .as("Возвращается не верный пользователь с в качестве друга.")
                .hasFieldOrPropertyWithValue("login", "friend");
        assertThat(friend2)
                .as("Возвращается не верный пользователь в качестве друга.")
                .hasFieldOrPropertyWithValue("login", "common");

    }

    @Test
    public void shouldFindCommonFriends(){
        friendDao.addToFriends(1L, 3L);
        friendDao.addToFriends(2L, 3L);

        List<User> actualCommonFriends = friendDao.findCommonFriends(1L, 2L);
        User actualCommonFriend = actualCommonFriends.get(0);

        assertThat(actualCommonFriends.size())
                .as("Возвращается не верное количество общих друзей.")
                .isEqualTo(1);
        assertThat(actualCommonFriend)
                .as("Возвращается не верный пользоветель в качестве общего друга.")
                .hasFieldOrPropertyWithValue( "login", "common");
    }
    @Test
    public void shouldDeleteFromFriends() {
        friendDao.addToFriends(1L, 2L);
        friendDao.addToFriends(1L, 3L);

        friendDao.deleteFromFriends(1L, 2);
        List<User> actualFriends = friendDao.findAllFriends(1L);
        User actualFriend = actualFriends.get(0);

        assertThat(actualFriends.size())
                .as("Пользоветель не был удален из друзей.")
                .isEqualTo(1);

        assertThat(actualFriend)
                .as("Был удален не верный пользоветель в качестве друга.")
                .hasFieldOrPropertyWithValue("login", "common");
    }
}
