package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean available;

    @NotNull
    private User owner;

    private ItemRequest request;
}
