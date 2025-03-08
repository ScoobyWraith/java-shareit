package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User itemOwner;

    @BeforeEach
    public void beforeEach() {
        itemOwner = userRepository.save(new User(null, "tester", "test@test.com"));
    }

    @AfterEach
    public void afterEach() {
        userRepository.delete(itemOwner);
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void saveTest() {
        Item item = new Item(null, "name", "desc", true, itemOwner, null);
        Item savedItem = itemRepository.save(item);

        Assertions.assertEquals(item.getName(), savedItem.getName());
        Assertions.assertEquals(item.getDescription(), savedItem.getDescription());
        Assertions.assertEquals(item.getOwner().getId(), savedItem.getOwner().getId());
    }

    @Test
    public void search_whenTwoItemsHasSearchText_thenGetTwoItems() {
        itemRepository
                .save(new Item(null, "ttt WORD qqq", "desc", true, itemOwner, null));
        itemRepository
                .save(new Item(null, "name", "aa word bbb", true, itemOwner, null));

        List<Item> searched = itemRepository.search("word");

        Assertions.assertEquals(2, searched.size());
        Assertions.assertTrue(searched.stream().anyMatch(item -> item.getName().contains("WORD")));
        Assertions.assertTrue(searched.stream().anyMatch(item -> item.getDescription().contains("word")));
    }

    @Test
    public void findAllByOwnerId_whenOwnerHasTwoItems_thenGetTwoItems() {
        itemRepository
                .save(new Item(null, "item 1", "desc", true, itemOwner, null));
        itemRepository
                .save(new Item(null, "item 2", "desc", true, itemOwner, null));

        List<Item> itemsOfOwner = itemRepository.findAllByOwnerId(itemOwner.getId());

        Assertions.assertEquals(2, itemsOfOwner.size());
    }

    @Test
    public void findAllByRequestIn_whenRequestForOneItem_thenGetOneItems() {
        ItemRequest itemRequest
                = itemRequestRepository.save(new ItemRequest(null, "a", itemOwner, LocalDateTime.now()));
        itemRepository
                .save(new Item(null, "item 1", "desc", true, itemOwner, null));
        itemRepository
                .save(new Item(null, "item 2", "desc", true, itemOwner, itemRequest));

        List<Item> itemsByRequest = itemRepository.findAllByRequestIn(List.of(itemRequest));

        Assertions.assertEquals(1, itemsByRequest.size());
    }
}