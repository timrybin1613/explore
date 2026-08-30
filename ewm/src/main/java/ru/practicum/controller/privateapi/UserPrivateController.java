package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserPrivacySettingsDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class UserPrivateController {
    private final UserService userService;

    @PatchMapping("/settings/privacy")
    public UserDto updatePrivacySettings(
            @PathVariable Long userId,
            @RequestBody UserPrivacySettingsDto dto) {

        return userService.setPrivacySettings(userId, dto);
    }

    @GetMapping("/subscriptions")
    public List<UserShortDto> getSubscriptionsForUser(@PathVariable Long userId) {
        return userService.getSubscriptionsForUser(userId);
    }

    @GetMapping("/subscribers")
    public List<UserShortDto> getSubscribersForUser(@PathVariable Long userId) {
        return userService.getSubscribersForUser(userId);
    }
}
