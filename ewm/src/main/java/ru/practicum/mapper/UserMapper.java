package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.user.CreateUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Component
public class UserMapper {

    public User toUser(CreateUserDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public List<UserDto> toDto(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}
