package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.EmailRepeated;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserServiceImpl(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserCreateDto userDto) {
        User user = userMapper.fromUserCreateDto(userDto);
        checkEmailRepeat(user);
        return userMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto get(Long id) {
        User user = getUserWithExistingCheck(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User user = getUserWithExistingCheck(userId);
        User updatedUser = userMapper.fromUserDto(userDto);

        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
            checkEmailRepeat(user);
        }

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }

        return userMapper.toUserDto(userStorage.update(user));
    }

    @Override
    public UserDto delete(Long id) {
        User user = getUserWithExistingCheck(id);
        userStorage.deleteById(id);
        return userMapper.toUserDto(user);
    }

    private User getUserWithExistingCheck(Long id) {
        Optional<User> userOpt = userStorage.getById(id);

        if (userOpt.isEmpty()) {
            throw new NotFound(String.format("User with id %d not found", id));
        }

        return userOpt.get();
    }

    private void checkEmailRepeat(User user) {
        Optional<User> userOpt = userStorage.getByEmail(user.getEmail());

        if (userOpt.isPresent() && !userOpt.get().getId().equals(user.getId())) {
            throw new EmailRepeated("Field email must be unique");
        }
    }
}
