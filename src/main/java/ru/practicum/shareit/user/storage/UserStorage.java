package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserStorage {
    User create(User user);
    User update(User user);
    Optional<User> getById(Long id);
    Optional<User> getByEmail(String email);
    void deleteById(Long id);
}
