package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@SpringJUnitConfig({ItemRequestServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final ItemRequestMapper itemRequestMapper;

    @MockBean
    private final ItemMapper itemMapper;

    @Test
    public void getAllRequestsWhenNoRequests() {
        User user = new User(1L, "tester", "test@test.com");

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user))
                .thenReturn(List.of());

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorNotOrderByCreatedDesc(user);
        Assertions.assertTrue(requests.isEmpty());
    }
}