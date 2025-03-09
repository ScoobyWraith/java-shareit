package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User booker;

    private List<Item> items;

    private Booking bookingInPast1;
    private Booking bookingInPast2;
    private Booking bookingInFeature;

    private LocalDateTime origin;

    @BeforeEach
    public void beforeEach() {
        booker = userRepository.save(new User(null, "booker", "booker@test.com"));
        User owner = userRepository.save(new User(null, "owner", "owner@test.com"));

        items = List.of(
                itemRepository.save(new Item(null, "1", "d", true, owner, null)),
                itemRepository.save(new Item(null, "2", "d", true, owner, null)),
                itemRepository.save(new Item(null, "3", "d", true, owner, null))
        );

        origin = LocalDateTime.now();

        bookingInPast1 = bookingRepository.save(new Booking(
                null,
                origin.minusDays(10),
                origin.plusDays(1),
                items.get(0),
                booker,
                BookingStatus.APPROVED
        ));
        bookingInPast2 = bookingRepository.save(new Booking(
                null,
                origin.minusDays(5),
                origin.plusDays(1),
                items.get(1),
                booker,
                BookingStatus.APPROVED
        ));
        bookingInFeature = bookingRepository.save(new Booking(
                null,
                origin.plusDays(3),
                origin.plusDays(5),
                items.get(2),
                booker,
                BookingStatus.APPROVED
        ));
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllLastBookingsForItemsTest() {
        List<Long> ids = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> lastBookingsForItems = bookingRepository.findAllLastBookingsForItems(origin, ids);

        Assertions.assertEquals(2, lastBookingsForItems.size());
        Assertions.assertTrue(lastBookingsForItems.stream().anyMatch(b -> b.getId().equals(bookingInPast1.getId())));
        Assertions.assertTrue(lastBookingsForItems.stream().anyMatch(b -> b.getId().equals(bookingInPast2.getId())));
    }

    @Test
    void findAllNearestNextBookingsForItemsTest() {
        List<Long> ids = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> lastBookingsForItems = bookingRepository.findAllNearestNextBookingsForItems(origin, ids);

        Assertions.assertEquals(1, lastBookingsForItems.size());
        Assertions.assertTrue(lastBookingsForItems.stream().anyMatch(b -> b.getId().equals(bookingInFeature.getId())));
    }

    @Test
    void findFirstByBookerAndItemAndStatusAndStartBeforeTest() {
        Optional<Booking> booking = bookingRepository.findFirstByBookerAndItemAndStatusAndStartBefore(
                booker,
                items.get(1),
                BookingStatus.APPROVED,
                origin
        );

        Assertions.assertTrue(booking.isPresent());
        Assertions.assertEquals(items.get(1).getId(), booking.get().getItem().getId());
        Assertions.assertEquals(booker.getId(), booking.get().getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, booking.get().getStatus());
    }
}