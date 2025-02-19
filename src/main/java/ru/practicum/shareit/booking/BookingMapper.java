package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BookingMapper {
    private static final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public BookingDto toBookingDto(Booking booking, ItemDto item, UserDto booker) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(dateTimeFormatter.format(booking.getStart()))
                .end(dateTimeFormatter.format(booking.getEnd()))
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }

    public Booking fromBookingCreateDto(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.parse(bookingCreateDto.getStart(), dateTimeFormatter))
                .end(LocalDateTime.parse(bookingCreateDto.getEnd(), dateTimeFormatter))
                .booker(booker)
                .item(item)
                .build();
    }
}
