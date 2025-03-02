package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BookingCreateDto {
    @NotNull
    private Long itemId;

    @NotBlank
    private String start;

    @NotBlank
    private String end;
}
