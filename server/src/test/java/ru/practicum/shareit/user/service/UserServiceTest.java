package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private final EntityManager em;

    @Test
    public void create_whenValidUser_thenSave() {
        UserCreateDto userDto = createUserCreateDto("test@test.com", "tester");

        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void create_whenEmailRepeat_thenThrowException() {
        UserCreateDto userDto1 = createUserCreateDto("test@test.com", "tester 1");
        UserCreateDto userDto2 = createUserCreateDto("test@test.com", "tester 2");

        userService.create(userDto1);

        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.create(userDto2)
        );
    }

    public static UserCreateDto createUserCreateDto(String email, String name) {
        return UserCreateDto.builder()
                .email(email)
                .name(name)
                .build();
    }
}