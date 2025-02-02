package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    Item create(Long ownerId, Item item) throws NotFound;
    Item get(Long id) throws NotFound;
    Item update(Long ownerId, Long itemId, Item item) throws NotFound, IllegalOwner;
    Item delete(Long ownerId, Long itemId) throws NotFound, IllegalOwner;
    List<Item> search(String searchText);
    List<Item> getByOwner(Long ownerId) throws NotFound;
}
