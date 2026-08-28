package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.subscription.SubscriptionService;
import ru.practicum.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserPublicController {
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/{userId}/subscriptions/{targetId}")
    List<UserShortDto> getSubscriptionsForUser(@PathVariable Long userId,
                                               @PathVariable Long targetId) {
        return userService.getSubscriptionsForUserPublic(userId, targetId);
    }
}
