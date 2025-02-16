package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailRepeated;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        User user = userMapper.fromUserCreateDto(userCreateDto);
        checkEmailRepeat(user);
        return userMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto get(Long id) {
        return userMapper.toUserDto(userStorage.getById(id));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) throws NotFound {
        User user = userStorage.getById(userId);

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
            checkEmailRepeat(user);
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return userMapper.toUserDto(userStorage.update(user));
    }

    @Override
    public UserDto delete(Long id) throws NotFound {
        User user = userStorage.getById(id);
        userStorage.deleteById(id);
        return userMapper.toUserDto(user);
    }

    private void checkEmailRepeat(User user) {
        try {
            User presentedUser = userStorage.getByEmail(user.getEmail());

            if (!presentedUser.getId().equals(user.getId())) {
                throw new EmailRepeated("Field email must be unique");
            }
        } catch (NotFound ignored) { }
    }
}
