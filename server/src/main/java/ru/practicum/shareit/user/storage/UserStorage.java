package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User getById(Long id) throws NotFound;

    User getByEmail(String email) throws NotFound;

    void deleteById(Long id) throws NotFound;
}
