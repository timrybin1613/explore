package ru.practicum.service.subscription;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

@Service
@Slf4j
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void addSubscription(Long userId, Long targetUserId) {
        getUserOrThrow(userId);
        User targetUser = getUserOrThrow(targetUserId);

        if (!Boolean.TRUE.equals(targetUser.getSubscriptionsAllowed())) {
            throw new ConflictException("Subscriptions are not allowed");
        }

        if (userId.equals(targetUserId)) {
            throw new ConflictException("Self subscriptions are not allowed");
        }

        subscriptionRepository.addSubscription(userId, targetUserId);
    }

    @Transactional
    @Override
    public void removeSubscription(Long userId, Long targetUserId) {
        getUserOrThrow(userId);
        getUserOrThrow(targetUserId);

        int deleted = subscriptionRepository.deleteSubscription(userId, targetUserId);

        if (deleted == 0) {
            throw new NotFoundException("Subscription not found");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
