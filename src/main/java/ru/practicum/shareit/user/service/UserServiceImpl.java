package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailRepeated;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User user) {
        checkEmailRepeat(user);
        return userStorage.create(user);
    }

    @Override
    public User get(Long id) {
        return getUserWithExistingCheck(id);
    }

    @Override
    public User update(User updatedUser, Long userId) throws NotFound {
        User user = getUserWithExistingCheck(userId);

        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
            checkEmailRepeat(user);
        }

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }

        return userStorage.update(user);
    }

    @Override
    public User delete(Long id) throws NotFound {
        User user = getUserWithExistingCheck(id);
        userStorage.deleteById(id);
        return user;
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
