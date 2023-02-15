package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private Set<Long> friends =  new HashSet<>();

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
