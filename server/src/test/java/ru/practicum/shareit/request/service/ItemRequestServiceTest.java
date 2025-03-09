package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.service.ServiceTest;

import java.util.List;

import static org.mockito.Mockito.when;

@Nested
@SpringJUnitConfig({ItemRequestServiceImpl.class})
class ItemRequestServiceTest extends ServiceTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void createRequestTest() {
        ItemRequestDto request
                = itemRequestService.createRequest(requestor.getId(), new ItemRequestCreateDto("request"));

        Assertions.assertNotNull(request);
        Assertions.assertEquals("request", request.getDescription());
    }

    @Test
    void getRequestsForOwnerTest() {
        when(itemRequestRepository.findAllByRequestorIsOrderByCreatedDesc(ArgumentMatchers.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> requestsForOwner = itemRequestService.getRequestsForOwner(requestor.getId());

        Assertions.assertEquals(1, requestsForOwner.size());
    }

    @Test
    void getAllRequestsTest() {
        when(itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(ArgumentMatchers.any()))
                .thenReturn(List.of(itemRequest));
        Item item = itemsOfOwner2.getLast();

        List<ItemRequestDto> requestsForOwner = itemRequestService.getAllRequests(requestor.getId());

        Assertions.assertEquals(1, requestsForOwner.size());
        Assertions.assertEquals(item.getId(), requestsForOwner.getFirst().getItems().getFirst().getId());
    }

    @Test
    void getRequestByIdTest() {
        ItemRequestDto request = itemRequestService.getRequestById(itemRequest.getId());

        Assertions.assertNotNull(request);
        Assertions.assertEquals(itemRequest.getId(), request.getId());
        Assertions.assertEquals(itemRequest.getCreated(), request.getCreated());
        Assertions.assertEquals(itemRequest.getDescription(), request.getDescription());
    }
}
