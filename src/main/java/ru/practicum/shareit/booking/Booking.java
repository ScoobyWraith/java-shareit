package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Booking {
    private Long id;

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    private BookingStatus status;
}
