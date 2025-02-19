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
import ru.practicum.shareit.util.RepositoryUtil;

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
        User user = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long id) throws NotFound {
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, id);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto updatedItem) throws NotFound, IllegalOwner {
        User user = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, itemId);
        RepositoryUtil.checkOwnerRightsForItem(item, user);

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
        User user = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        Item item = RepositoryUtil.getItemWithCheck(itemRepository, itemId);
        RepositoryUtil.checkOwnerRightsForItem(item, user);
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
        RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        return itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
