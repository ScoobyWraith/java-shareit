package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String name;

    @Email(message = "Field email must contains valid email-address")
    private String email;
}
