package ru.practicum.service.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.CreateUserDto;
import ru.practicum.dto.user.UserDto;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.UserPrivacySettingsDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserDto dto);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    void deleteUser(Long id);

    List<UserShortDto> getSubscriptionsForUserPublic(Long userId, Long targetId);

    List<UserShortDto> getSubscriptionsForUser(Long userId);

    List<UserShortDto> getSubscribersForUser(Long userId);

    @Transactional
    UserDto setPrivacySettings(Long userId, UserPrivacySettingsDto dto);
}
