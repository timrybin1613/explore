package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RequestMapper {

    public Request toRequest(User requester,
                             Event event,
                             RequestStatus status,
                             LocalDateTime created) {
        return Request.builder()
                .created(created)
                .event(event)
                .requester(requester)
                .status(status)
                .build();
    }

    public RequestDto toDto(Request request) {
        return RequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public List<RequestDto> toDto(List<Request> requests) {
        return requests.stream().map(this::toDto).toList();
    }
}
