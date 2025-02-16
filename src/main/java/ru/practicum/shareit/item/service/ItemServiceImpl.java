package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) throws NotFound {
        User user = getUserWithCheck(ownerId);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long id) throws NotFound {
        Item item = getItemWithCheck(id);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto updatedItem) throws NotFound, IllegalOwner {
        User user = getUserWithCheck(ownerId);
        Item item = getItemWithCheck(itemId);
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

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto delete(Long ownerId, Long itemId) throws NotFound, IllegalOwner {
        User user = getUserWithCheck(ownerId);
        Item item = getItemWithCheck(itemId);
        checkOwnerRights(item, user);
        itemRepository.deleteById(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        return itemRepository.search(searchText)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwner(Long ownerId) throws NotFound {
        getUserWithCheck(ownerId);
        return itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    private void checkOwnerRights(Item item, User user) {
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new IllegalOwner(String.format("User %d has not rights for item %d", user.getId(), item.getId()));
        }
    }

    private User getUserWithCheck(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFound(String.format("User with id %d not found.", id)));
    }

    private Item getItemWithCheck(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFound(String.format("Item with id %d not found.", id)));
    }
}
