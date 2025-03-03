package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
									@Valid @RequestBody BookItemRequestDto bookingCreateDto) {
		log.info("Request from user {} to book item: {}", userId, bookingCreateDto);
		return bookingClient.createBooking(userId, bookingCreateDto);
	}

	@PatchMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
											@RequestParam Boolean approved,
											@PathVariable Long bookingId) {
		log.info("Request to set booking approve for {} to '{}' from owner {}", bookingId, approved, ownerId);
		return bookingClient.approveBooking(ownerId, approved, bookingId);
	}

	@GetMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
								 @PathVariable Long bookingId) {
		log.info("Request to get booking {} by user {}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllBookingsWithState(@RequestHeader("X-Sharer-User-Id") long userId,
													@RequestParam(defaultValue = "all", name = "state") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown booking state: " + stateParam));
		log.info("Request to get all bookings with state '{}' for user {}", state, userId);
		return bookingClient.getAllBookingsWithState(userId, state);
	}

	@GetMapping("/owner")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllBookingsOfItemsForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
														  @RequestParam(defaultValue = "all", name = "state") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown booking state: " + stateParam));
		log.info("Request to get all bookings with state '{}' for owner {}", state, ownerId);
		return bookingClient.getAllBookingsOfItemsForOwner(ownerId, state);
	}
}
