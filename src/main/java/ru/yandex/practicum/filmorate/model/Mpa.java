package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Mpa {
    private int id;
    private String name;

    @JsonIgnore
    private String description;
}
