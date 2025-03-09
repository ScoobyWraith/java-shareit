package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private static final String API_PREFIX = "/requests";

    @MockBean
    private final ItemRequestService itemRequestService;

    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "description",
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            null
    );

    @Test
    void createRequestTest() throws Exception {
        Mockito
                .when(itemRequestService.createRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post(API_PREFIX)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .createRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    void getRequestsForOwnerTest() throws Exception {
        Mockito
                .when(itemRequestService.getRequestsForOwner(ArgumentMatchers.anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get(API_PREFIX)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1L), Long.class));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestsForOwner(ArgumentMatchers.anyLong());
    }

    @Test
    void getAllRequestsTest() throws Exception {
        Mockito
                .when(itemRequestService.getAllRequests(ArgumentMatchers.anyLong()))
                .thenReturn(List.of(itemRequestDto, itemRequestDto));

        mvc.perform(get(API_PREFIX + "/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2L), Long.class));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllRequests(ArgumentMatchers.anyLong());
    }

    @Test
    void getRequestByIdTest() throws Exception {
        Mockito
                .when(itemRequestService.getRequestById(2L))
                .thenReturn(itemRequestDto);

        mvc.perform(get(API_PREFIX + "/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestById(2L);
    }
}
