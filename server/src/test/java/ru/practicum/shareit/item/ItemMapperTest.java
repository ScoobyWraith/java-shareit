package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOnlyDatesDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemMapperTest {
    private final JacksonTester<ItemWithBookingAndCommentsDto> itemWithBookingAndCommentsDtoJson;

    private final ItemMapper itemMapper = new ItemMapper();

    private User user;
    private Item item;
    private List<CommentDto> comments;
    private BookingOnlyDatesDto lastBooking;
    private BookingOnlyDatesDto nearestBooking;

    @BeforeEach
    public void beforeEach() {
        String dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        user = new User(1L, "name", "email");
        item = new Item(1L, "name", "desc", true, user, null);
        comments = List.of(
                new CommentDto(1L, "text", dateTime, "author"),
                new CommentDto(2L, "text", dateTime, "author"),
                new CommentDto(3L, "text", dateTime, "author")
        );
        lastBooking = new BookingOnlyDatesDto(1L, dateTime, dateTime);
        nearestBooking = new BookingOnlyDatesDto(1L, dateTime, dateTime);
    }

    @Test
    void toItemWithBookingDtoTest() throws IOException {
        ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = itemMapper.toItemWithBookingDto(
                item, lastBooking, nearestBooking, comments
        );

        JsonContent<ItemWithBookingAndCommentsDto> result
                = itemWithBookingAndCommentsDtoJson.write(itemWithBookingAndCommentsDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(lastBooking.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(nearestBooking.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments.length()").isEqualTo(comments.size());
    }
}
