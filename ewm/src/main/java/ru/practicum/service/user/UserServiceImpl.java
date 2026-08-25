package ru.practicum.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.CreateUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
}
