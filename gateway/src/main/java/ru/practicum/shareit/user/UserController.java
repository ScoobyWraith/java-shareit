package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Request to add user: {}", userCreateDto);
        return userClient.create(userCreateDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Request to update user: {}", userDto);

        if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new IllegalArgumentException("Field 'name' can't be blank");
        }

        if (userDto.getEmail() != null && userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Field 'email' can't be blank");
        }

        return userClient.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Request to get user with id {}", userId);
        return userClient.get(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Request to delete user with id {}", userId);
        return userClient.delete(userId);
    }
}
