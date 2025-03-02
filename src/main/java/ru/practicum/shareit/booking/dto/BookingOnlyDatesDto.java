package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BookingOnlyDatesDto {
    private Long id;
    private String start;
    private String end;
}
