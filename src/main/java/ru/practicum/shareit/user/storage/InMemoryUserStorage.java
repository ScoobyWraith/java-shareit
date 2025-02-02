package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user = user.toBuilder().build();
        user.setId(generateNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        user = user.toBuilder().build();
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id).toBuilder().build());
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    private long generateNewId() {
        return users.keySet().stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
