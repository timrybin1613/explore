package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createUserRequestPrivate(@PathVariable Long userId,
                                               @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> findUserRequestsPrivate(@PathVariable Long userId) {
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelUserRequestPrivate(@PathVariable Long userId,
                                               @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}