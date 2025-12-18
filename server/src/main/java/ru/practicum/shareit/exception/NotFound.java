package ru.practicum.shareit.exception;

public class NotFound extends RuntimeException {
    public NotFound(final String msg) {
        super(msg);
    }
}
