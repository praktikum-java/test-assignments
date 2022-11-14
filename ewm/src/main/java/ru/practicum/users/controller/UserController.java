package ru.practicum.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin.Пользователи", description = "API для работы с пользователями.")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получение информации о пользователях",
            description = "Возвращает list of UserDto. " +
                    "Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), " +
                    "либо о конкретных (учитываются указанные идентификаторы)")
    // . */
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,      // id пользователей
                                  @PositiveOrZero(message = "'from' must be positive or zero")
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @Positive(message = "'size' must be positive")
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received a request: GET /admin/users with parameters: ids = {} from = {} " +
                "size = {}", Arrays.toString(ids), from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @Operation(summary = "Добавление нового пользователя", description = "Возвращает UserDto")
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Received a request: POST /admin/users with body = {}", newUserRequest);
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя", description = "Возвращает только статус ответа или ошибку")
    public void removeUser(@Positive(message = "'userId' must be positive")
                           @PathVariable Long userId) {
        log.info("Received a request: DELETE /admin/users/{}", userId);
        userService.removeUser(userId);
    }
}