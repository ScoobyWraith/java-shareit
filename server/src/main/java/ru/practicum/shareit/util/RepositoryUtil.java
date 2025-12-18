package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

public class RepositoryUtil {
    public static User getUserWithCheck(UserRepository userRepository, Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFound(String.format("User with id %d not found.", id)));
    }

    public static Item getItemWithCheck(ItemRepository itemRepository, Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFound(String.format("Item with id %d not found.", id)));
    }

    public static void checkOwnerRightsForItem(Item item, User user) {
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new IllegalOwner(String.format("User %d has not rights for item %d", user.getId(), item.getId()));
        }
    }

    public static ItemRequest getItemRequestWithCheck(ItemRequestRepository itemRequestRepository, Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFound(String.format("Item request with id %d not found.", id)));
    }
}
