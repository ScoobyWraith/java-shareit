package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Field name can't be empty")
    private String name;

    @NotBlank(message = "Field email can't be empty")
    @Email(message = "Field email must contains valid email-address")
    private String email;
}
