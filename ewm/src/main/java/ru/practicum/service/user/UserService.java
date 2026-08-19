package ru.practicum.service.user;

import ru.practicum.dto.user.CreateUserDto;
import ru.practicum.dto.user.UserDto;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserDto dto);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    void deleteUser(Long id);
}
