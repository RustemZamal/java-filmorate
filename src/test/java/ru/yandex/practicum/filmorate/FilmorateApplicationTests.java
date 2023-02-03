package ru.yandex.practicum.filmorate;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmorateApplicationTests {
	@Value(value="${local.server.port}")
	private int port;
	@Autowired
	private UserController userController;
	@Autowired
	private FilmController filmController;

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private FilmStorage filmStorage;
	HttpClient client = HttpClient.newHttpClient();



	@Test
	void contextLoads() {
		assertThat(filmController).isNotNull();
	}

	@AfterEach
	public void deleteUsersAndFilms() {
		userStorage.deleteAllUsers();
		filmStorage.deleteAllFilms();
	}

	@Test
	public void shouldAddUser() throws IOException, InterruptedException {
		User user = User.builder()
				.login("login")
				.name("User")
				.email("email@email.ru")
				.birthday(LocalDate.of(1990, 7, 2))
				.build();

		post(user);
		assertEquals(
				1, userController.getAllUsers().size(), "Неверное количество пользователей");
	}

	@Test
	public void shouldAddFilm() throws IOException, InterruptedException {
		Film film = Film.builder()
				.name("Suits")
				.releaseDate(LocalDate.of(1990, 7, 23))
				.duration(200)
				.description("Description")
				.build();

		post(film);

		assertEquals(
				1, filmController.getAllFilms().size(),
				"Неверное количество фильмов");
	}

	@Test
	public void shouldNotAddUserWithInvalidLogin() throws IOException, InterruptedException {
		User userInvalidLogin = User.builder()
				.login("lo gin")
				.name("User")
				.email("email@email.ru")
				.birthday(LocalDate.of(1990, 7, 2))
				.build();

		post(userInvalidLogin);

		assertEquals(
				0, userController.getAllUsers().size(), "Пользователь добавляется с неверным логином");
	}

	@Test
	public void shouldNotAddUserWithInvalidEmail() throws IOException, InterruptedException {
		User userInvalidEmail = User.builder()
				.login("login")
				.name("User")
				.email("email?email.ru@")
				.birthday(LocalDate.of(1990, 7, 2))
				.build();
		User userInvalidEmailBlank = User.builder()
				.login("login")
				.name("User")
				.email("  ")
				.birthday(LocalDate.of(1990, 7, 2))
				.build();
		User userInvalidEmailNull = User.builder()
				.login("login")
				.name("User")
				.birthday(LocalDate.of(1990, 7, 2))
				.build();

		post(userInvalidEmail);
		post(userInvalidEmailNull);
		post(userInvalidEmailBlank);

		assertEquals(0,
				userController.getAllUsers().size(), "Пользователь добавляется с неверным email");
	}

	@Test
	public void shouldNotAddUserWithInvalidBirthday() throws IOException, InterruptedException {
		User userInvalidLogin = User.builder()
				.login("login")
				.name("User")
				.email("email@email.ru")
				.birthday(LocalDate.now().plusYears(100))
				.build();

		post(userInvalidLogin);

		assertEquals(
				0, userController.getAllUsers().size(), "Дата рождения не должна быть в будущем");
	}

	@Test
	public void shouldNotAddFilmWithInvalidName() throws IOException, InterruptedException {
		Film filmInvalidName = Film.builder()
				.name("  ")
				.description("Description")
				.duration(200)
				.releaseDate(LocalDate.of(1990, 7, 23))
				.build();

		Film filmInvalidNameNull = Film.builder()
				.description("Description")
				.duration(200)
				.releaseDate(LocalDate.of(1990, 7, 23))
				.build();

		post(filmInvalidName);
		post(filmInvalidNameNull);


		assertEquals(
				0, filmController.getAllFilms().size(), "Добавился фильм с недопустимым названием");
	}

	@Test
	public void shouldNotAddFilmWithInvalidDescription() throws IOException, InterruptedException {
		Film filmInvalidDescription = Film.builder()
				.name("Suits")
				.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
						"Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги," +
						" а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
						"стал кандидатом Коломбани.")
				.duration(200)
				.releaseDate(LocalDate.of(1990, 7, 23))
				.build();

		post(filmInvalidDescription);

		assertEquals(
				0, filmController.getAllFilms().size(),
				"Добавился фильм с описанием свыше 200 символов");
	}

	@Test
	public void shouldNotAddFilmWithInvalidDuration() throws IOException, InterruptedException {
		Film filmInvalidDuration = Film.builder()
				.name("Suits")
				.description("Description")
				.duration(-1)
				.releaseDate(LocalDate.of(1990, 7, 23))
				.build();

		post(filmInvalidDuration);

		assertEquals(
				0, filmController.getAllFilms().size(),
				"Добавился фильм с отрицательной продолжительностью");
	}

	@Test
	public void shouldNotAddFilmWithInvalidReleaseDate() throws IOException, InterruptedException {
		Film filmInvalidReleaseDate = Film.builder()
				.name("Suits")
				.description("Description")
				.duration(200)
				.releaseDate(LocalDate.of(1895, 12, 27))
				.build();

		post(filmInvalidReleaseDate);

		assertEquals(
				0, filmController.getAllFilms().size(),
				"Добавился фильм с недопустимым годом выпуска");
	}


	public void post(User user) throws IOException, InterruptedException {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
		String userJson = gson.toJson(user);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userJson);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/users"))
				.POST(body)
				.header("Content-Type","application/json")
				.version(HttpClient.Version.HTTP_1_1)
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		response.body();
	}

	public void post(Film film) throws IOException, InterruptedException {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
		String filmJson = gson.toJson(film);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(filmJson);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/films"))
				.POST(body)
				.header("Content-Type","application/json")
				.version(HttpClient.Version.HTTP_1_1)
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		response.body();
	}

}
