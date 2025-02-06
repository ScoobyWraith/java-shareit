package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item = item
                .toBuilder()
                .build();
        item.setId(generateNewId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFound(String.format("Item with id %d not found.", id));
        }

        return items.get(id)
                .toBuilder()
                .build();
    }

    @Override
    public Item update(Item item) {
        item = item
                .toBuilder()
                .build();
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFound(String.format("Item with id %d not found.", id));
        }

        items.remove(id);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String searchText) {
        return items.values().stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(searchText.toLowerCase())
                        || item.getDescription().toLowerCase().contains(searchText.toLowerCase())))
                .toList();
    }

    private long generateNewId() {
        return items.keySet()
                .stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
