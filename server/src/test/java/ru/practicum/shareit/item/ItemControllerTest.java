package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private static final String API_PREFIX = "/items";

    @MockBean
    private final ItemService itemService;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "name",
            "desc",
            true,
            2L
    );

    private final CommentDto commentDto = new CommentDto(
            1L,
            "text",
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
            "author name"
    );

    private final ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = new ItemWithBookingAndCommentsDto(
            1L,
            "name",
            "desc",
            true,
            null,
            null,
            null
    );

    @Test
    void createItemTest() throws Exception {
        Mockito
                .when(itemService.create(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(itemDto);

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
        Mockito.verify(itemService, Mockito.times(1))
                .create(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    void updateItemTest() throws Exception {
        Mockito
                .when(itemService.update(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(itemDto);

        mvc.perform(patch(API_PREFIX + "/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
        Mockito.verify(itemService, Mockito.times(1))
                .update(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    void addCommentTest() throws Exception {
        Mockito
                .when(itemService.addComment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(commentDto);

        mvc.perform(post(API_PREFIX + "/1/comment")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
        Mockito.verify(itemService, Mockito.times(1))
                .addComment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    void getItemTest() throws Exception {
        Mockito
                .when(itemService.get(ArgumentMatchers.anyLong()))
                .thenReturn(itemWithBookingAndCommentsDto);

        mvc.perform(get(API_PREFIX + "/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingAndCommentsDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemWithBookingAndCommentsDto.getDescription())))
                .andExpect(jsonPath("$.name", is(itemWithBookingAndCommentsDto.getName())))
                .andExpect(jsonPath("$.available", is(itemWithBookingAndCommentsDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.comments", is(itemWithBookingAndCommentsDto.getComments())))
                .andExpect(jsonPath("$.lastBooking", is(itemWithBookingAndCommentsDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemWithBookingAndCommentsDto.getNextBooking())));
        Mockito.verify(itemService, Mockito.times(1))
                .get(ArgumentMatchers.anyLong());
    }

    @Test
    void getAllItemsByOwnerTest() throws Exception {
        Mockito
                .when(itemService.getByOwner(ArgumentMatchers.anyLong()))
                .thenReturn(List.of(itemWithBookingAndCommentsDto));

        mvc.perform(get(API_PREFIX)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1L), Long.class))
                .andExpect(jsonPath("$.[0].id", is(itemWithBookingAndCommentsDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemWithBookingAndCommentsDto.getDescription())))
                .andExpect(jsonPath("$.[0].name", is(itemWithBookingAndCommentsDto.getName())))
                .andExpect(jsonPath("$.[0].available", is(itemWithBookingAndCommentsDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.[0].comments", is(itemWithBookingAndCommentsDto.getComments())))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemWithBookingAndCommentsDto.getLastBooking())))
                .andExpect(jsonPath("$.[0].nextBooking", is(itemWithBookingAndCommentsDto.getNextBooking())));
        Mockito.verify(itemService, Mockito.times(1))
                .getByOwner(ArgumentMatchers.anyLong());
    }

    @Test
    void searchItemsTest() throws Exception {
        Mockito
                .when(itemService.search("word"))
                .thenReturn(List.of(itemDto));

        mvc.perform(get(API_PREFIX + "/search?text=word")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1L), Long.class))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable()), Boolean.class));
        Mockito.verify(itemService, Mockito.times(1))
                .search("word");
    }
}