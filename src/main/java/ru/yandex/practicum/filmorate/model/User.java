package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;

    @NotBlank(message = "Логин не должен содержать пробелы или быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotBlank(message = "email не должен содержать пробелы или быть пустым")
    @Email(message = "Неверный email. Пожалуйста введите верный email.")
    private String email;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
