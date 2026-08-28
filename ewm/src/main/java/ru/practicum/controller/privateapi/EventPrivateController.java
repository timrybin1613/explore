package ru.practicum.controller.privateapi;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.CreateEventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.UpdateEventUserRequestDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatusUpdateRequestDto;
import ru.practicum.dto.request.RequestStatusUpdateResultDto;
import ru.practicum.service.event.EventService;
import ru.practicum.util.PageUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findEventsPrivate(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsByUserId(userId, PageUtils.fromOffset(from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@PathVariable Long userId,
                                           @Valid @RequestBody CreateEventDto dto) {
        return eventService.createEvent(dto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventPrivate(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return eventService.getEventByIdForUser(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequestDto dto) {
        return eventService.updateEventPrivate(eventId, userId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> findEventRequestsPrivate(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        return eventService.getRequestsByEventIdAndUserId(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResultDto updateEventRequestStatusPrivate(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @Valid @RequestBody RequestStatusUpdateRequestDto dto) {
        return eventService.updateRequestStatus(dto, userId, eventId);
    }

    @GetMapping("/subscriptions")
    public List<EventShortDto> findAllSubscriptionsPrivate(@PathVariable Long userId,
                                                           @RequestParam(defaultValue = "0") Integer from,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsBySubscriptions(userId, PageUtils.fromOffset(from, size));
    }
}
