package ru.yandex.practicum.filmorate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        if (localDate != null)
            jsonWriter.value(localDate.format(FORMATTER));
        else
            jsonWriter.nullValue();

    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        if (jsonReader == null)
            return LocalDate.parse("null");
        return LocalDate.parse(jsonReader.nextString(), FORMATTER);
    }
}
