package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@Data
public class Film {
    protected int id;

    @NotBlank(message = "Название не должно быть пустым")
    @NotNull(message = "Название не может быть null")
    protected final String name;

    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов")
    protected final String description;

    protected final LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private long duration;
}
