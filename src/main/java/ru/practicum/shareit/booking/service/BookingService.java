package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto);

    BookingDto processBookingByOwner(long ownerId, Boolean approved, long bookingId);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getAllBookingsWithState(long userId, BookingState state);

    List<BookingDto> getAllBookingsOfItemsForOwner(long ownerId, BookingState state);
}
