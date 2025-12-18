package ru.practicum.shareit.request.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private User requestor1;
    private User requestor2;

    private ItemRequest request1ByRequster1;
    private ItemRequest request2ByRequster1;
    private ItemRequest request1ByRequster2;

    @BeforeEach
    public void beforeEach() {
        requestor1 = userRepository.save(new User(null, "requestor1", "requestor1@test.com"));
        requestor2 = userRepository.save(new User(null, "requestor2", "requestor2@test.com"));

        request1ByRequster1
                = itemRequestRepository.save(new ItemRequest(null, "d1", requestor1, LocalDateTime.now()));
        request2ByRequster1
                = itemRequestRepository.save(new ItemRequest(null, "d2", requestor1, LocalDateTime.now()));
        request1ByRequster2
                = itemRequestRepository.save(new ItemRequest(null, "d3", requestor2, LocalDateTime.now()));
    }

    @AfterEach
    public void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIsOrderByCreatedDescTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIsOrderByCreatedDesc(requestor1);

        Assertions.assertEquals(2, requests.size());
        Assertions.assertEquals(requests.get(0).getId(), request2ByRequster1.getId());
        Assertions.assertEquals(requests.get(1).getId(), request1ByRequster1.getId());
    }

    @Test
    void findAllByRequestorNotOrderByCreatedDescTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(requestor1);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requests.get(0).getId(), request1ByRequster2.getId());
    }
}