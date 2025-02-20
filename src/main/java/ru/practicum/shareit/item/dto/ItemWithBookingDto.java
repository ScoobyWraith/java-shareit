package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOnlyDatesDto;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemWithBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingOnlyDatesDto lastBooking;
    private BookingOnlyDatesDto nearestBooking;
}
