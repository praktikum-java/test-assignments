package ru.practicum.users.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserMapperImpl;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;

    Integer from = 0;
    Integer size = 10;
    Long[] ids = {1L, 2L};
    Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
    User user1 = User.builder().id(1L).name("User1").email("user1@gmail.com").build();
    User user2 = User.builder().id(2L).name("User2").email("user2@gmail.com").build();
    UserDto userDto1 = UserDto.builder().id(1L).name("User1").email("user1@gmail.com").build();
    UserDto userDto2 = UserDto.builder().id(2L).name("User2").email("user2@gmail.com").build();
    List<User> users = List.of(user1, user2);
    Page<User> page = new PageImpl<>(users);

    // getUsers
    @Test
    public void shouldReturnUserDtoWhoIncludedInTheList() {
        when(userRepository.findAllByIdIn(List.of(ids), pageable)).thenReturn(page);
        List<UserDto> result = userService.getUsers(ids, from, size);
        List<UserDto> listToCheck = List.of(userDto1, userDto2);
        assertEquals(listToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithComments() {
        when(userRepository.findAll(pageable)).thenReturn(page);
        List<UserDto> result = userService.getUsers(null, from, size);
        List<UserDto> listToCheck = List.of(userDto1, userDto2);
        assertEquals(listToCheck, result);
    }

    // createUser
    @Test
    public void shouldCreateUserAndReturnDto() {
        NewUserRequest newUserRequest = NewUserRequest.builder().name("User1").email("user1@gmail.com").build();
        User user0 = User.builder().id(null).name("User1").email("user1@gmail.com").build();
        when(userRepository.save(user0)).thenReturn(user1);
        UserDto result = userService.createUser(newUserRequest);
        UserDto userToCheck = userDto1;
        assertEquals(userToCheck, result);
    }

    // removeUser
    @Test
    public void shouldRemoveUserAndCallRepository() {
        userRepository.deleteById(1L);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    // getEntityUserById
    @Test
    public void shouldGetUserEntity() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        User result = userService.getEntityUserById(1L);
        User userToCheck = user1;
        assertEquals(userToCheck, result);
    }

    @Test
    public void shouldGetNotFindEntityUserByIdAndThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> userService.getEntityUserById(99L));
        assertEquals("User not found", re.getMessage());
    }
}
