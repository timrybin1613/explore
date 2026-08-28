package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.subscription.SubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/subscriptions/{targetId}")
public class SubscriptionPrivateController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@PathVariable Long userId,
                          @PathVariable Long targetId) {
        subscriptionService.addSubscription(userId, targetId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable Long userId,
                            @PathVariable Long targetId) {
        subscriptionService.removeSubscription(userId, targetId);
    }
}
