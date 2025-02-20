package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto) throws NotFound;

    ItemDto get(Long id) throws NotFound;

    ItemDto update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) throws NotFound, IllegalOwner;

    ItemDto delete(Long ownerId, Long itemId) throws NotFound, IllegalOwner;

    List<ItemDto> search(String searchText);

    List<ItemWithBookingDto> getByOwner(Long ownerId) throws NotFound;
}
