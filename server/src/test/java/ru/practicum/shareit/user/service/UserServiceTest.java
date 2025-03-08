package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

@SpringJUnitConfig({UserServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final UserMapper userMapper;

    @Test
    public void createTest() {
        UserCreateDto userCreateDto = new UserCreateDto("tester", "test@test.com");
        User user = new User(1L, "tester", "test@test.com");
        UserDto userDto = new UserDto(1L, "tester", "test@test.com");

        Mockito
                .when(userMapper.fromUserCreateDto(userCreateDto))
                .thenReturn(user);
        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);
        Mockito
                .when(userMapper.toUserDto(user))
                .thenReturn(userDto);

        userService.create(userCreateDto);

        Mockito.verify(userMapper, Mockito.times(1))
                .fromUserCreateDto(userCreateDto);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user);
        Mockito.verify(userMapper, Mockito.times(1))
                .toUserDto(user);
    }
}