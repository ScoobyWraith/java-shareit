package ru.practicum.shareit.exception;

public class IllegalOwner extends RuntimeException {
    public IllegalOwner(String message) {
        super(message);
    }
}
