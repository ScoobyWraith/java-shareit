package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

class RepositoryUtilTest {

    @Test
    void checkOwnerRightsForItem() {
        User owner = new User(1L, "owner", "email");
        User user = new User(2L, "user", "email");
        Item item = new Item(1L, "item", "desc", true, owner, null);

        Assertions.assertThrows(IllegalOwner.class, () -> {
            RepositoryUtil.checkOwnerRightsForItem(item, user);
        });
    }

    @Test
    void getUserWithCheck_whenUserNotFound() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFound.class, () -> {
            RepositoryUtil.getUserWithCheck(userRepository, 1L);
        });
    }

    @Test
    void getItemWithCheck_whenItemNotFound() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFound.class, () -> {
            RepositoryUtil.getItemWithCheck(itemRepository, 1L);
        });
    }

    @Test
    void getItemRequestWithCheck_whenItemRequestNotFound() {
        ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);

        Mockito.when(itemRequestRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFound.class, () -> {
            RepositoryUtil.getItemRequestWithCheck(itemRequestRepository, 1L);
        });
    }
}
