package ru.practicum.shareit.controller.advice;

import jakarta.validation.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BookingUnavailable;
import ru.practicum.shareit.exception.EmailRepeated;
import ru.practicum.shareit.exception.IllegalComment;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;

import java.util.stream.Collectors;

@RestControllerAdvice
public class Handler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidException(final MethodArgumentNotValidException e) {
        String body = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("\n"));
        return new ErrorResponse("Validation exception", body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("Validation exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalComment(final IllegalComment e) {
        return new ErrorResponse("Illegal comment", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFound e) {
        return new ErrorResponse("Not found exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailRepeated(final EmailRepeated e) {
        return new ErrorResponse("Conflict exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalOwner(final IllegalOwner e) {
        return new ErrorResponse("Access deny", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingUnavailable(final BookingUnavailable e) {
        return new ErrorResponse("Unavailable booking", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAnyException(final Exception e) {
        return new ErrorResponse("Exception", e.getMessage());
    }
}
