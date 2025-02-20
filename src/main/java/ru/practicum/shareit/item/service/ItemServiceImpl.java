package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingOnlyDatesDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.IllegalComment;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.RepositoryUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) throws NotFound {
        User user = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingAndCommentsDto get(Long id) throws NotFound {
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, id);
        return createItemWithBookingAndCommentsDtoList(List.of(item))
                .getFirst();
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto updatedItem) throws NotFound, IllegalOwner {
        User user = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, itemId);
        RepositoryUtil.checkOwnerRightsForItem(item, user);

        if (updatedItem.getName() != null) {
            item.setName(updatedItem.getName());
        }

        if (updatedItem.getDescription() != null) {
            item.setDescription(updatedItem.getDescription());
        }

        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto delete(Long ownerId, Long itemId) throws NotFound, IllegalOwner {
        User user = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, itemId);
        RepositoryUtil.checkOwnerRightsForItem(item, user);
        itemRepository.deleteById(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        return itemRepository.search(searchText)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingAndCommentsDto> getByOwner(Long ownerId) throws NotFound {
        RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        if (items.isEmpty()) {
            return List.of();
        }

        return createItemWithBookingAndCommentsDtoList(items);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User user = RepositoryUtil.getUserWithCheck(userRepository, userId);
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, itemId);

        Optional<Booking> finishedBooking = bookingRepository.findFirstByBookerAndItemAndStatusAndEndBefore(
                user,
                item,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );

        if (finishedBooking.isEmpty()) {
            throw new IllegalComment("Comment may be created only by user who booked this item");
        }

        Comment comment = commentMapper.fromCommentCreateDto(commentCreateDto, user, item);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    private List<ItemWithBookingAndCommentsDto> createItemWithBookingAndCommentsDtoList(List<Item> items) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, BookingOnlyDatesDto> lastBookingsForItemsMap = bookingRepository
                .findAllLastBookingsForItems(now, itemIds)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), bookingMapper::toBookingOnlyDatesDto));

        Map<Long, BookingOnlyDatesDto> nearestNextBookingsForItemsMap = bookingRepository
                .findAllNearestNextBookingsForItemsMap(now, itemIds)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), bookingMapper::toBookingOnlyDatesDto));

        Map<Long, List<CommentDto>> commentsForItemsMap = commentRepository.findAllByItemIn(items)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                comment -> comment.getItem().getId(),
                                Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())
                        )
                );

        return items.stream()
                .map(item -> {
                    BookingOnlyDatesDto lastBooking = lastBookingsForItemsMap.get(item.getId());
                    BookingOnlyDatesDto nearestNextBooking = nearestNextBookingsForItemsMap.get(item.getId());
                    List<CommentDto> comments = commentsForItemsMap.get(item.getId());
                    return itemMapper.toItemWithBookingDto(item, lastBooking, nearestNextBooking, comments);
                })
                .toList();
    }
}
