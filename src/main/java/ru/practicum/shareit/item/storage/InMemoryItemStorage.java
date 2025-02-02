package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item = item.toBuilder().build();
        item.setId(generateNewId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> get(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Item update(Item item) {
        item = item.toBuilder().build();
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(ownerId)).toList();
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
        return items.keySet().stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
