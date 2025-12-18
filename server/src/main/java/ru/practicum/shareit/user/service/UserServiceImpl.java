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
import ru.practicum.shareit.util.RepositoryUtil;

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
        User user = RepositoryUtil.getUserWithCheck(userRepository, id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) throws NotFound {
        User user = RepositoryUtil.getUserWithCheck(userRepository, userId);

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
        User user = RepositoryUtil.getUserWithCheck(userRepository, id);
        userRepository.deleteById(id);
        return userMapper.toUserDto(user);
    }
}
