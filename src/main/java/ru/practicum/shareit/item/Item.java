package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    Long id;

    @NotBlank
    String name;

    @NotBlank
    String description;

    boolean available;

    @NotNull
    User owner;

    ItemRequest request;
}
