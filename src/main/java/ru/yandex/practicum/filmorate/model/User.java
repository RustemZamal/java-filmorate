package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    protected int id;

    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не должен содержать пробелы")
    protected String login;

    protected String name;

    @NotBlank(message = "email не должен содержать пробелы")
    @NotNull(message = "email не может быть пустым")
    @Email(message = "Неверный email. Пожалуйста введите верный email.")
    protected String email;

    @Past(message = "Дата рождения не может быть в будущем")
    protected LocalDate birthday;
}
