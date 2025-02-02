package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;

public interface UserService {
    User create(User user);

    User get(Long id) throws NotFound;

    User update(User user, Long userId) throws NotFound;

    User delete(Long id) throws NotFound;
}
