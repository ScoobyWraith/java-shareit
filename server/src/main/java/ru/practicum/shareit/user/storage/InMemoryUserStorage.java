package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user = user
                .toBuilder()
                .build();
        user.setId(generateNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        user = user
                .toBuilder()
                .build();
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFound(String.format("User with id %d not found.", id));
        }

        return users.get(id)
                .toBuilder()
                .build();
    }

    @Override
    public User getByEmail(String email) {
        Optional<User> userOpt = users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        if (userOpt.isEmpty()) {
            throw new NotFound(String.format("User with email %s not found.", email));
        }

        return userOpt.get()
                .toBuilder()
                .build();
    }

    @Override
    public void deleteById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFound(String.format("User with id %d not found.", id));
        }

        users.remove(id);
    }

    private long generateNewId() {
        return users.keySet().stream()
                .max(Long::compare)
                .orElse(0L) + 1;
    }
}
