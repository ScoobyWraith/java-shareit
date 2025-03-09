package ru.practicum.shareit.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringJUnitConfig({
        BookingMapper.class,
        UserMapper.class,
        CommentMapper.class,
        ItemRequestMapper.class,
        ItemMapper.class})
public class ServiceTest {
    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected ItemRepository itemRepository;

    @MockBean
    protected ItemRequestRepository itemRequestRepository;

    @MockBean
    protected CommentRepository commentRepository;

    @MockBean
    protected BookingRepository bookingRepository;

    protected User ownerOfItems1;
    protected User ownerOfItems2;
    protected User requestor;
    protected User booker;

    protected List<Item> itemsOfOwner1;
    protected List<Item> itemsOfOwner2;

    protected List<Booking> bookingsForItemsOfOwner1;

    protected List<Comment> commentsForItemsOfOwner1;

    protected ItemRequest itemRequest;

    protected LocalDateTime originNow;

    @BeforeEach
    public void beforeEach() {
        originNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ownerOfItems1 = new User(1L, "owner 1", "email");
        ownerOfItems2 = new User(2L, "owner 2", "email");
        requestor = new User(3L, "requestor", "email");
        booker = new User(4L, "booker", "email");

        itemRequest = new ItemRequest(1L, "desc", requestor, originNow);

        itemsOfOwner1 = List.of(
                new Item(1L, "item", "desc", true, ownerOfItems1, null),
                new Item(2L, "item", "desc", true, ownerOfItems1, null),
                new Item(3L, "item", "desc", true, ownerOfItems1, null)
        );

        itemsOfOwner2 = List.of(
                new Item(4L, "item", "desc", true, ownerOfItems2, null),
                new Item(5L, "item", "desc", true, ownerOfItems2, null),
                new Item(6L, "item", "desc", true, ownerOfItems2, itemRequest)
        );

        bookingsForItemsOfOwner1 = List.of(
                new Booking(1L, originNow.minusDays(10), originNow.minusDays(9), itemsOfOwner1.get(0), booker,
                        BookingStatus.APPROVED),
                new Booking(2L, originNow.minusDays(5), originNow.minusDays(4), itemsOfOwner1.get(1), booker,
                        BookingStatus.APPROVED),
                new Booking(3L, originNow.plusDays(2), originNow.plusDays(3), itemsOfOwner1.get(2), booker,
                        BookingStatus.WAITING)
        );

        commentsForItemsOfOwner1 = List.of(
                new Comment(1L, "comment", originNow.minusDays(8), booker, itemsOfOwner1.get(0)),
                new Comment(2L, "comment", originNow.minusDays(2), booker, itemsOfOwner1.get(1))
        );

        // find by id
        when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(ownerOfItems1));

        when(itemRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(itemsOfOwner1.getFirst()));

        when(bookingRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(bookingsForItemsOfOwner1.getFirst()));

        when(itemRequestRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        when(commentRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(commentsForItemsOfOwner1.getFirst()));

        // find all
        when(bookingRepository.findAll(ArgumentMatchers.any(Predicate.class), ArgumentMatchers.any(OrderSpecifier.class)))
                .thenReturn(bookingsForItemsOfOwner1);

        // save
        when(bookingRepository.save(ArgumentMatchers.any()))
                .thenAnswer((m) -> m.getArgument(0));

        when(userRepository.save(ArgumentMatchers.any()))
                .thenAnswer((m) -> m.getArgument(0));

        when(itemRepository.save(ArgumentMatchers.any()))
                .thenAnswer((m) -> m.getArgument(0));

        when(itemRequestRepository.save(ArgumentMatchers.any()))
                .thenAnswer((m) -> m.getArgument(0));

        when(commentRepository.save(ArgumentMatchers.any()))
                .thenAnswer((m) -> m.getArgument(0));

        // custom
        when(itemRepository.findAllByOwnerId(ArgumentMatchers.anyLong()))
                .thenReturn(itemsOfOwner1);

        when(bookingRepository.findAllLastBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(bookingsForItemsOfOwner1.subList(0, 2));

        when(bookingRepository.findAllNearestNextBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(bookingsForItemsOfOwner1.subList(2, 3));

        when(itemRepository.findAllByRequestIn(ArgumentMatchers.any()))
                .thenReturn(List.of(itemsOfOwner2.getLast()));
    }
}
