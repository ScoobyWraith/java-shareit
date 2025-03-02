package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Request from user {} to book item: {}", userId, bookingCreateDto);
        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto processBookingByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @RequestParam Boolean approved,
                                            @PathVariable Long bookingId) {
        log.info("Request to set booking approve for {} to '{}' from owner {}", bookingId, approved, ownerId);
        return bookingService.processBookingByOwner(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long bookingId) {
        log.info("Request to get booking {} by user {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsWithState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Request to get all bookings with state '{}' for user {}", state, userId);
        return bookingService.getAllBookingsWithState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfItemsForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Request to get all bookings with state '{}' for owner {}", state, ownerId);
        return bookingService.getAllBookingsOfItemsForOwner(ownerId, state);
    }
}
