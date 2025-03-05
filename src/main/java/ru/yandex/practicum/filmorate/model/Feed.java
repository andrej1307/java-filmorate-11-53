package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feed {

    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private Operation operation;
    private Integer eventId;
    private Integer entityId;
}