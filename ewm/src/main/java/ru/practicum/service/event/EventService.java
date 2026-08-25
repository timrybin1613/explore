package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatusUpdateRequestDto;
import ru.practicum.dto.request.RequestStatusUpdateResultDto;

import java.util.List;

public interface EventService {
    @Transactional
    EventFullDto createEvent(CreateEventDto dto, Long userId);

    List<EventShortDto> getEventsByUserId(Long userId, Pageable pageable);

    EventFullDto getEventByIdForUser(Long eventId, Long userId);

    @Transactional
    EventFullDto updateEventPrivate(Long eventId,
                                    Long userId,
                                    UpdateEventUserRequestDto dto);

    List<RequestDto> getRequestsByEventIdAndUserId(Long eventId, Long userId);

    @Transactional
    RequestStatusUpdateResultDto updateRequestStatus(RequestStatusUpdateRequestDto dto,
                                                     Long userId,
                                                     Long eventId);

    List<EventFullDto> getEventsAdmin(EventAdminSearchParams params);

    @Transactional
    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequestDto dto);

    List<EventShortDto> getEventsPublic(EventPublicSearchParams params,
                                        HttpServletRequest request,
                                        Pageable pageable);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
