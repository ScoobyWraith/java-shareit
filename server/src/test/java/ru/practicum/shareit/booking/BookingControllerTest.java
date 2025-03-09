package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingUnavailable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private static final String API_PREFIX = "/bookings";

    @MockBean
    private final BookingService bookingService;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
            LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS).toString(),
            new ItemDto(1L, "name", "desc", true, null),
            new UserDto(1L, "name", "email"),
            BookingStatus.WAITING
    );

    @Test
    void createBookingTest() throws Exception {
        Mockito
                .when(bookingService.createBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(bookingDto);

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())));
        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    void createBooking_throwBookingUnavailable() throws Exception {
        Mockito
                .when(bookingService.createBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenThrow(new BookingUnavailable("booking"));

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unavailable booking")))
                .andExpect(jsonPath("$.description", is("booking")));
        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    void processBookingByOwnerTest() throws Exception {
        Mockito
                .when(bookingService.processBookingByOwner(1L, true, 1L))
                .thenReturn(bookingDto);

        mvc.perform(patch(API_PREFIX + "/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())));
        Mockito.verify(bookingService, Mockito.times(1))
                .processBookingByOwner(1L, true, 1L);
    }

    @Test
    void getBookingTest() throws Exception {
        Mockito
                .when(bookingService.getBooking(1L, 1L))
                .thenReturn(bookingDto);

        mvc.perform(get(API_PREFIX + "/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())));
        Mockito.verify(bookingService, Mockito.times(1))
                .getBooking(1L, 1L);
    }

    @Test
    void getAllBookingsWithStateTest() throws Exception {
        Mockito
                .when(bookingService.getAllBookingsWithState(1L, BookingState.ALL))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get(API_PREFIX + "?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1L), Long.class))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.[0].item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart())));
        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsWithState(1L, BookingState.ALL);
    }

    @Test
    void getAllBookingsOfItemsForOwnerTest() throws Exception {
        Mockito
                .when(bookingService.getAllBookingsOfItemsForOwner(1L, BookingState.ALL))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get(API_PREFIX + "/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1L), Long.class))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.[0].item", is(bookingDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart())));
        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsOfItemsForOwner(1L, BookingState.ALL);
    }
}