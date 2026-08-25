package ru.practicum.service.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        log.debug("getRequestsByUserId({})", userId);
        User user = getUserOrThrow(userId);

        return requestMapper.toDto(requestRepository.findAllByRequesterId(user.getId()));
    }

    @Transactional
    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.debug("createRequest({}, {})", userId, eventId);

        User requester = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        LocalDateTime now = LocalDateTime.now();
        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new ConditionsNotMetException("The event initiator cannot participate in their own event.");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionsNotMetException("The event state is not published.");
        }

        if (event.getParticipantLimit() > 0
                && event.getParticipantLimit() <= getCountRequestsByEventId(eventId, RequestStatus.CONFIRMED)) {
            throw new ConditionsNotMetException("The event participant limit is too large");
        }

        RequestStatus requestStatus = RequestStatus.PENDING;

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = RequestStatus.CONFIRMED;
        }

        return requestMapper.toDto(
                requestRepository.save(Request.builder()
                        .created(truncateToMicros(now))
                        .status(requestStatus)
                        .requester(requester)
                        .event(event)
                        .build()));
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(Long requesterId, Long requestId) {
        log.debug("cancelRequest({}, {})", requesterId, requestId);

        Request request = getRequestOrThrow(requestId, requesterId);
        if (request.getStatus() == RequestStatus.CANCELED) {
            throw new ConditionsNotMetException("The request has already been canceled.");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(request);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private long getCountRequestsByEventId(Long eventId, RequestStatus status) {
        return requestRepository.countByEventIdAndStatus(eventId, status);
    }

    private Request getRequestOrThrow(Long requestId, Long requesterId) {
        return requestRepository.findByIdAndRequesterId(requestId, requesterId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
    }

    private LocalDateTime truncateToMicros(LocalDateTime dateTime) {
        return dateTime.withNano((dateTime.getNano() / 1000) * 1000);
    }
}
