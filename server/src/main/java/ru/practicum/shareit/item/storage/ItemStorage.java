package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item getById(Long id) throws NotFound;

    Item update(Item item);

    void deleteById(Long id) throws NotFound;

    List<Item> getByOwner(Long ownerId);

    List<Item> search(String searchText);
}
