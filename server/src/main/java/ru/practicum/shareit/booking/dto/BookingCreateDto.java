package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BookingCreateDto {
    private Long itemId;
    private String start;
    private String end;
}
