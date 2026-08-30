package ru.practicum.service.subscription;

import org.springframework.transaction.annotation.Transactional;

public interface SubscriptionService {
    @Transactional
    void addSubscription(Long userId, Long targetUserId);

    @Transactional
    void removeSubscription(Long userId, Long targetUserId);
}
