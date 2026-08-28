package ru.practicum.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.CreateUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserPrivacySettingsDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        log.debug("Creating user {}", dto);
        return userMapper.toDto(userRepository.save(userMapper.toUser(dto)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        log.debug("Retrieving users by ids {}", ids);
        if (ids == null || ids.isEmpty()) {
            return userMapper.toDto(userRepository.findAll(pageable).getContent());
        } else {
            return userMapper.toDto(userRepository.findByIdIn(ids));
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserShortDto> getSubscriptionsForUserPublic(Long userId, Long targetId) {
        getUserOrThrow(userId);

        User targetUser = getUserOrThrow(targetId);

        if (!targetUser.getSubscriptionsPublic()) {
            throw new ConflictException("Subscriptions are not public");
        }

        List<Long> subscriptionIds = subscriptionRepository.findTargetUserIds(targetId);

        List<User> users = userRepository.findAllById(subscriptionIds);

        return userMapper.toShortDto(users);
    }

    @Override
    public List<UserShortDto> getSubscriptionsForUser(Long userId) {
        getUserOrThrow(userId);

        List<Long> subscriptionIds = subscriptionRepository.findTargetUserIds(userId);

        List<User> users = userRepository.findAllById(subscriptionIds);

        return userMapper.toShortDto(users);
    }

    @Override
    public List<UserShortDto> getSubscribersForUser(Long userId) {
        getUserOrThrow(userId);

        List<Long> subscribersIds = subscriptionRepository.findSubscribersForUser(userId);

        List<User> users = userRepository.findAllById(subscribersIds);

        return userMapper.toShortDto(users);
    }

    @Transactional
    @Override
    public UserDto setPrivacySettings(Long userId, UserPrivacySettingsDto dto) {
        User user = getUserOrThrow(userId);

        if (dto.getSubscriptionsAllowed() != null) {
            user.setSubscriptionsAllowed(dto.getSubscriptionsAllowed());
        }

        if (dto.getSubscriptionsPublic() != null) {
            user.setSubscriptionsPublic(dto.getSubscriptionsPublic());
        }

        return userMapper.toDto(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
