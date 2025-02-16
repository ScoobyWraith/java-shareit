package ru.practicum.shareit.exception;

public class EmailRepeated extends RuntimeException {
    public EmailRepeated(String message) {
        super(message);
    }
}
