package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.QBooking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingUnavailable;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.RepositoryUtil;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto) {
        LocalDateTime now = Util.getNowTruncatedToSeconds();
        User user = RepositoryUtil.getUserWithCheck(userRepository, userId);
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, bookingCreateDto.getItemId());
        Booking booking = bookingMapper.fromBookingCreateDto(bookingCreateDto, item, user);

        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BookingUnavailable(String.format("Incorrect data times: " +
                    "start date %s is after then end date %s", booking.getStart(), booking.getEnd()));
        }

        if (booking.getStart().isBefore(now)) {
            throw new BookingUnavailable(String.format("Incorrect data times: " +
                    "start date %s in past rel now %s", booking.getStart(), now));
        }

        if (booking.getEnd().isBefore(now)) {
            throw new BookingUnavailable(String.format("Incorrect data times: " +
                    "end date %s in past rel now %s", booking.getEnd(), now));
        }

        if (!item.getAvailable()) {
            throw new BookingUnavailable(String.format("Item with id %d is not available", item.getId()));
        }

        bookingRepository.save(booking);
        return buildBookingDto(booking);
    }

    @Override
    public BookingDto processBookingByOwner(long ownerId, Boolean approved, long bookingId) {
        Booking booking = getBookingWithCheck(bookingId);
        Item item = booking.getItem();
        User owner = item.getOwner();

        if (!owner.getId().equals(ownerId)) {
            throw new BookingUnavailable("(Un)Approve booking can only the owner");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingUnavailable("Only for booking with status WAITING can set APPROVED or REJECTED status");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return buildBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = getBookingWithCheck(bookingId);
        User booker = booking.getBooker();
        User itemOwner = booking.getItem().getOwner();

        if (booker.getId().equals(userId) || itemOwner.getId().equals(userId)) {
            return buildBookingDto(booking);
        }

        throw new IllegalOwner(String.format("User with id %d has no rights to watch" +
                " booking with id %d", userId, bookingId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsWithState(long userId, BookingState state) {
        LocalDateTime now = Util.getNowTruncatedToSeconds();
        RepositoryUtil.getUserWithCheck(userRepository, userId);
        BooleanExpression conditions = QBooking.booking.booker.id.eq(userId);
        Optional<BooleanExpression> additionalConditions = getBookingConditionsByState(now, state);

        if (additionalConditions.isPresent()) {
            conditions = conditions.and(additionalConditions.get());
        }

        Iterable<Booking> bookings = bookingRepository.findAll(conditions, QBooking.booking.end.desc());
        return toOrderedDtoList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsOfItemsForOwner(long ownerId, BookingState state) {
        LocalDateTime now = Util.getNowTruncatedToSeconds();
        RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId);

        if (userItems.isEmpty()) {
            throw new NotFound(String.format("User with id %d have not items", ownerId));
        }

        BooleanExpression conditions = QBooking.booking.item.in(userItems);
        Optional<BooleanExpression> additionalConditions = getBookingConditionsByState(now, state);

        if (additionalConditions.isPresent()) {
            conditions = conditions.and(additionalConditions.get());
        }

        Iterable<Booking> bookings = bookingRepository.findAll(conditions, QBooking.booking.end.desc());
        return toOrderedDtoList(bookings);
    }

    private Booking getBookingWithCheck(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFound(String.format("Booking with id %d not found", bookingId)));
    }

    private BookingDto buildBookingDto(Booking booking) {
        return bookingMapper.toBookingDto(
                booking,
                itemMapper.toItemDto(booking.getItem()),
                userMapper.toUserDto(booking.getBooker())
        );
    }

    private List<BookingDto> toOrderedDtoList(Iterable<Booking> iterableBookings) {
        ArrayList<BookingDto> result = new ArrayList<>();
        iterableBookings.forEach(booking -> result.add(buildBookingDto(booking)));
        return result;
    }

    private Optional<BooleanExpression> getBookingConditionsByState(LocalDateTime now, BookingState state) {
        BooleanExpression dateTimeExpression;
        BooleanExpression statusExpression;
        QBooking qBooking = QBooking.booking;

        switch (state) {
            case ALL:
                return Optional.empty();

            case CURRENT:
                dateTimeExpression = qBooking.start.before(now).and(qBooking.end.after(now));
                statusExpression = qBooking.status.eq(BookingStatus.APPROVED);
                return Optional.of(dateTimeExpression.and(statusExpression));

            case PAST:
                dateTimeExpression = qBooking.end.before(now);
                statusExpression = qBooking.status.eq(BookingStatus.APPROVED);
                return Optional.of(dateTimeExpression.and(statusExpression));

            case FUTURE:
                dateTimeExpression = qBooking.start.after(now);
                statusExpression = qBooking.status.eq(BookingStatus.APPROVED);
                return Optional.of(dateTimeExpression.and(statusExpression));

            case WAITING:
                statusExpression = qBooking.status.eq(BookingStatus.WAITING);
                return Optional.of(statusExpression);

            case REJECTED:
                statusExpression = qBooking.status.eq(BookingStatus.REJECTED);
                return Optional.of(statusExpression);

            default:
                throw new RuntimeException(String.format("Unsupported state '%s' for booking expression", state));
        }
    }
}
