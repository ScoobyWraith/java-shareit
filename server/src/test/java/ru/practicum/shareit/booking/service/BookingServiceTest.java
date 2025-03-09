package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingUnavailable;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.service.ServiceTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@Nested
@SpringJUnitConfig({BookingServiceImpl.class})
class BookingServiceTest extends ServiceTest {
    @Autowired
    private BookingService bookingService;

    @Test
    void createBookingTest() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                itemsOfOwner1.getFirst().getId(),
                originNow.plusDays(20).toString(),
                originNow.plusDays(21).toString()
        );

        BookingDto bookingDto = bookingService.createBooking(booker.getId(), bookingCreateDto);

        Assertions.assertEquals(bookingCreateDto.getItemId(), bookingDto.getItem().getId());
        Assertions.assertEquals(bookingCreateDto.getStart(), bookingDto.getStart());
        Assertions.assertEquals(bookingCreateDto.getEnd(), bookingDto.getEnd());
    }

    @Test
    void createBooking_whenStartAfterEnd_throwBookingUnavailable() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                itemsOfOwner1.getFirst().getId(),
                originNow.plusDays(22).toString(),
                originNow.plusDays(21).toString()
        );

        Assertions.assertThrows(BookingUnavailable.class, () -> {
            bookingService.createBooking(booker.getId(), bookingCreateDto);
        });
    }

    @Test
    void createBooking_whenStartBeforeNow_throwBookingUnavailable() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                itemsOfOwner1.getFirst().getId(),
                originNow.minusDays(22).toString(),
                originNow.plusDays(21).toString()
        );

        Assertions.assertThrows(BookingUnavailable.class, () -> {
            bookingService.createBooking(booker.getId(), bookingCreateDto);
        });
    }

    @Test
    void createBooking_whenItemIsNotAvailable_throwBookingUnavailable() {
        Item item = itemsOfOwner1.getFirst();
        item.setAvailable(false);
        when(itemRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(item));

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                itemsOfOwner1.getFirst().getId(),
                originNow.plusDays(20).toString(),
                originNow.plusDays(21).toString()
        );

        Assertions.assertThrows(BookingUnavailable.class, () -> {
            bookingService.createBooking(booker.getId(), bookingCreateDto);
        });
    }

    @Test
    void processBookingByOwnerTest() {
        Booking booking = bookingsForItemsOfOwner1.getLast();

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.processBookingByOwner(
                ownerOfItems1.getId(),
                false,
                bookingsForItemsOfOwner1.getLast().getId()
        );

        Assertions.assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }

    @Test
    void processBookingByOwner_whenStatusIsNotWaiting_throwBookingUnavailable() {
        Booking booking = bookingsForItemsOfOwner1.getFirst();

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Assertions.assertThrows(BookingUnavailable.class, () -> {
            bookingService.processBookingByOwner(
                    ownerOfItems1.getId(),
                    false,
                    bookingsForItemsOfOwner1.getLast().getId()
            );
        });
    }

    @Test
    void processBookingByOwner_whenIllegalOwner_throwBookingUnavailable() {
        Booking booking = bookingsForItemsOfOwner1.getFirst();


        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Assertions.assertThrows(BookingUnavailable.class, () -> {
            bookingService.processBookingByOwner(
                    100,
                    false,
                    bookingsForItemsOfOwner1.getLast().getId()
            );
        });
    }

    @Test
    void getBookingTest() {
        Booking booking = bookingsForItemsOfOwner1.getFirst();
        BookingDto bookingDto = bookingService.getBooking(ownerOfItems1.getId(), booking.getId());

        Assertions.assertEquals(booking.getStatus(), bookingDto.getStatus());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        Assertions.assertEquals(booking.getStart().toString(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd().toString(), bookingDto.getEnd());
    }

    @Test
    void getAllBookingsWithStateAllTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsWithState(booker.getId(), BookingState.ALL);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsWithStateCurrentTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsWithState(booker.getId(), BookingState.CURRENT);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsWithStatePastTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsWithState(booker.getId(), BookingState.PAST);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsWithStateFutureTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsWithState(booker.getId(), BookingState.FUTURE);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsWithStateWatingTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsWithState(booker.getId(), BookingState.WAITING);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsWithStateRejectTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsWithState(booker.getId(), BookingState.REJECTED);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsOfItemsForOwnerTest() {
        List<BookingDto> allBookingsOfItemsForOwner
                = bookingService.getAllBookingsOfItemsForOwner(ownerOfItems1.getId(), BookingState.ALL);

        Assertions.assertEquals(3, allBookingsOfItemsForOwner.size());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(0).getId(), allBookingsOfItemsForOwner.get(0).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(1).getId(), allBookingsOfItemsForOwner.get(1).getId());
        Assertions.assertEquals(bookingsForItemsOfOwner1.get(2).getId(), allBookingsOfItemsForOwner.get(2).getId());
    }

    @Test
    void getAllBookingsOfItemsForOwner_whenItemsEmpty_thenThrowNotFound() {
        when(itemRepository.findAllByOwnerId(ArgumentMatchers.anyLong()))
                .thenReturn(List.of());

        Assertions.assertThrows(NotFound.class, () -> {
            bookingService.getAllBookingsOfItemsForOwner(ownerOfItems1.getId(), BookingState.ALL);
        });
    }
}