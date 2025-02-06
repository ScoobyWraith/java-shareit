package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) throws NotFound {
        User user = userStorage.getById(ownerId);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto get(Long id) throws NotFound {
        return itemMapper.toItemDto(itemStorage.getById(id));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto updatedItem) throws NotFound, IllegalOwner {
        User user = userStorage.getById(ownerId);
        Item item = itemStorage.getById(itemId);
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

        return itemMapper.toItemDto(itemStorage.update(item));
    }

    @Override
    public ItemDto delete(Long ownerId, Long itemId) throws NotFound, IllegalOwner {
        User user = userStorage.getById(ownerId);
        Item item = itemStorage.getById(itemId);
        checkOwnerRights(item, user);
        itemStorage.deleteById(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        return itemStorage.search(searchText)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) throws NotFound {
        userStorage.getById(ownerId);
        return itemStorage.getByOwner(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    private void checkOwnerRights(Item item, User user) {
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new IllegalOwner(String.format("User %d has not rights for item %d", user.getId(), item.getId()));
        }
    }
}
