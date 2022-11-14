package ru.practicum.users.dto;

import org.mapstruct.Mapper;
import ru.practicum.users.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(NewUserRequest newUserRequest);

    UserShortDto toUserShortDto(User user);
}
