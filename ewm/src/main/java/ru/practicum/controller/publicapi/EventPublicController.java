package ru.practicum.controller.publicapi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicSearchParams;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.event.EventService;
import ru.practicum.util.PageUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findEventsPublic(EventPublicSearchParams params,
                                                HttpServletRequest httpServletRequest,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.getEventsPublic(params, httpServletRequest, PageUtils.fromOffset(from, size));
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventPublic(@PathVariable Long eventId,
                                        HttpServletRequest httpServletRequest) {
        return eventService.getEventById(eventId, httpServletRequest);
    }
}
