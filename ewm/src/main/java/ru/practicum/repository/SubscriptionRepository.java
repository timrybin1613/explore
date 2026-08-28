package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findTargetUserIds(Long userId) {
        return jdbcTemplate.queryForList("""
                SELECT target_user_id
                FROM subscriptions
                WHERE subscriber_id = ?
                """, Long.class, userId);
    }

    public List<Long> findSubscribersForUser(Long userId) {
        return jdbcTemplate.queryForList("""
                SELECT subscriber_id
                FROM subscriptions
                WHERE target_user_id = ?
                """, Long.class, userId);
    }

    public void addSubscription(Long userId, Long targetUserId) {
        jdbcTemplate.update("""
                INSERT INTO subscriptions (subscriber_id, target_user_id)
                VALUES (?, ?)
                """, userId, targetUserId);
    }

    public int deleteSubscription(Long userId, Long targetUserId) {
        return jdbcTemplate.update("""
                DELETE FROM subscriptions
                WHERE subscriber_id = ?
                  AND target_user_id = ?
                """, userId, targetUserId);
    }
}