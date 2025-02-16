package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        User user = userMapper.fromUserCreateDto(userCreateDto);
        return userMapper.toUserDto(userRepository.save(user));
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        User user = getUserWithCheck(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) throws NotFound {
        User user = getUserWithCheck(userId);

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto delete(Long id) throws NotFound {
        User user = getUserWithCheck(id);
        userRepository.deleteById(id);
        return userMapper.toUserDto(user);
    }

    private User getUserWithCheck(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFound(String.format("User with id %d not found.", id)));
    }
}
