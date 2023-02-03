package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.annotation.AfterOrEqualDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    Set<Long> likesUserId = new HashSet<>();

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов")
    private String description;

    @AfterOrEqualDate(value = "1895-12-28", message = "Дата релиза фильма не соответствует параметрам")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private long duration;
}
