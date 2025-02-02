package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequest {
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private User requestor;

    private LocalDate created;
}
