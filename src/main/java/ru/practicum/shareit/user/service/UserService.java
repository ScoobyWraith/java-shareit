package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto create(UserCreateDto userDto);
    UserDto get(Long id);
    UserDto update(UserDto userDto, Long userId);
    UserDto delete(Long id);
}
