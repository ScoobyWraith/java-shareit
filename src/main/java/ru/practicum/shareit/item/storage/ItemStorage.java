package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Optional<Item> get(Long id);

    Item update(Item item);

    void delete(Long id);

    List<Item> getByOwner(Long ownerId);

    List<Item> search(String searchText);
}
