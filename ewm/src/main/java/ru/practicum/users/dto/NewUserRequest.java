package ru.practicum.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания нового пользователя")
public class NewUserRequest {
    @NotNull
    @Email
    @Schema(description = "Почтовый адрес")
    private String email;
    @NotBlank
    @Schema(description = "Имя пользователя")
    private String name;
}
