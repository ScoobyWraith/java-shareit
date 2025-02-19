package ru.practicum.shareit.exception;

public class BookingUnavailable extends RuntimeException {
    public BookingUnavailable(String message) {
        super(message);
    }
}
