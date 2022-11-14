package ru.practicum.users.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class UserDto {
    @NotNull
    @Email
    String email;
    Long id;
    @NotBlank
    String name;
}
