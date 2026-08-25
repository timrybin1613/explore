package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.CreateEventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toEvent(CreateEventDto dto,
                         Category category,
                         User initiator,
                         LocalDateTime createdOn,
                         EventState state) {

        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .initiator(initiator)
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .latitude(dto.getLocation().getLat())
                .longitude(dto.getLocation().getLon())
                .title(dto.getTitle())
                .createdOn(createdOn)
                .state(state)
                .build();

    }

    public EventFullDto toFullEventDto(Event event,
                                       CategoryDto categoryDto,
                                       UserShortDto initiator,
                                       Long views,
                                       Long confirmedRequests,
                                       LocationDto locationDto) {

        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .views(views)
                .confirmedRequests(confirmedRequests)
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState().toString())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(locationDto)
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public EventShortDto toEventShortDto(Event event,
                                         CategoryDto categoryDto,
                                         Long confirmedRequests,
                                         UserShortDto initiator,
                                         Long views) {

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }
}
