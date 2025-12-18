package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.service.ServiceTest;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

@Nested
@SpringJUnitConfig({UserServiceImpl.class})
class UserServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void createTest() {
        UserDto userDto = userService.create(new UserCreateDto("name", "email"));

        Assertions.assertNotNull(userDto);
    }

    @Test
    void getTest() {
        UserDto userDto = userService.get(ownerOfItems1.getId());

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(ownerOfItems1.getId(), userDto.getId());
        Assertions.assertEquals(ownerOfItems1.getName(), userDto.getName());
        Assertions.assertEquals(ownerOfItems1.getEmail(), userDto.getEmail());
    }

    @Test
    void updateTest() {
        UserDto userDto = userService.update(
                new UserDto(null, "new name", "new email"),
                ownerOfItems1.getId()
        );

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(ownerOfItems1.getId(), userDto.getId());
        Assertions.assertEquals("new name", userDto.getName());
        Assertions.assertEquals("new email", userDto.getEmail());
    }

    @Test
    void deleteTest() {
        UserDto userDto = userService.delete(ownerOfItems1.getId());

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(ownerOfItems1.getId(), userDto.getId());
        Assertions.assertEquals(ownerOfItems1.getName(), userDto.getName());
        Assertions.assertEquals(ownerOfItems1.getEmail(), userDto.getEmail());
    }
}
