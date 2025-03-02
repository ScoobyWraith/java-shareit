package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto create(UserCreateDto userCreateDto);

    UserDto get(Long id) throws NotFound;

    UserDto update(UserDto userDto, Long userId) throws NotFound;

    UserDto delete(Long id) throws NotFound;
}
