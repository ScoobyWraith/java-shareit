package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Item create(Long ownerId, Item item) throws NotFound {
        User user = userService.get(ownerId);
        item.setOwner(user);
        return itemStorage.create(item);
    }

    @Override
    public Item get(Long id) throws NotFound {
        return getItemWithExistingCheck(id);
    }

    @Override
    public Item update(Long ownerId, Long itemId, Item updatedItem) throws NotFound, IllegalOwner {
        User user = userService.get(ownerId);
        Item item = getItemWithExistingCheck(itemId);
        checkOwnerRights(item, user);

        if (updatedItem.getName() != null) {
            item.setName(updatedItem.getName());
        }

        if (updatedItem.getDescription() != null) {
            item.setDescription(updatedItem.getDescription());
        }

        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }

        return itemStorage.update(item);
    }

    @Override
    public Item delete(Long ownerId, Long itemId) throws NotFound, IllegalOwner {
        User user = userService.get(ownerId);
        Item item = getItemWithExistingCheck(itemId);
        checkOwnerRights(item, user);
        itemStorage.delete(itemId);
        return item;
    }

    @Override
    public List<Item> search(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        return itemStorage.search(searchText);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) throws NotFound {
        return itemStorage.getByOwner(ownerId);
    }

    private Item getItemWithExistingCheck(Long id) {
        Optional<Item> itemOpt = itemStorage.get(id);

        if (itemOpt.isEmpty()) {
            throw new NotFound(String.format("Item with id %d not found", id));
        }

        return itemOpt.get();
    }

    private void checkOwnerRights(Item item, User user) {
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new IllegalOwner(String.format("User %d has not rights for item %d", user.getId(), item.getId()));
        }
    }
}
