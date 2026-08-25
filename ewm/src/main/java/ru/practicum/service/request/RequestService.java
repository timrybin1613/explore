package ru.practicum.service.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequestsByUserId(Long userId);

    @Transactional
    RequestDto createRequest(Long userId, Long eventId);

    @Transactional
    RequestDto cancelRequest(Long requesterId, Long requestId);
}
