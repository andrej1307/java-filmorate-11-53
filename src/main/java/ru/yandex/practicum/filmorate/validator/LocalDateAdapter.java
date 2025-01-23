package ru.yandex.practicum.filmorate.validator;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Класс определения правил преобразования даты
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        if (localDate != null) {
            jsonWriter.value(localDate.format(dtf));
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        if (!value.equals("null")) {
            return LocalDate.parse(value, dtf);
        } else {
            return null;
        }
    }
}
