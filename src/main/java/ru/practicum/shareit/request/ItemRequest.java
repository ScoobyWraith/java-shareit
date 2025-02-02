package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class ItemRequest {
    Long id;

    @NotBlank
    String description;

    @NotNull
    User requestor;

    LocalDate created;
}
