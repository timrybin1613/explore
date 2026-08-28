package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatusUpdateRequestDto;
import ru.practicum.dto.request.RequestStatusUpdateResultDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import org.springframework.data.domain.Pageable;
import ru.practicum.util.PageUtils;


import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RequestMapper requestMapper;
    private final EventStatsService eventStatsService;
    private final SubscriptionRepository subscriptionRepository;
    private static final Set<EventState> EDITABLE_STATES = EnumSet.of(
            EventState.PENDING,
            EventState.CANCELED
    );

    @Transactional
    @Override
    public EventFullDto createEvent(CreateEventDto dto, Long userId) {
        log.debug("Creating event for user {}", userId);
        LocalDateTime now = LocalDateTime.now();
        if (dto.getEventDate().isBefore(now.plusHours(2))) {
            throw new ValidationException("Field: eventDate." +
                    " Error: event date must be at least two hours after the current time. Value: " + dto.getEventDate());
        }

        Category category = getCategoryOrThrow(dto.getCategory());
        User initiator = getUserOrThrow(userId);

        Event event = eventMapper.toEvent(
                dto,
                category,
                initiator,
                now,
                EventState.PENDING
        );

        event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        event.setParticipantLimit(
                dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0L
        );
        event.setRequestModeration(
                dto.getRequestModeration() != null ? dto.getRequestModeration() : true
        );

        Event savedEvent = eventRepository.save(event);

        return buildFullDto(savedEvent, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Pageable pageable) {
        log.debug("Getting events for user {}", userId);
        UserShortDto initiatorDto = userMapper.toUserShortDto(getUserOrThrow(userId));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(
                events.stream().map(Event::getId).toList());
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(events);

        return events.stream().map(event -> {
            Long confirmedRequests = requestsByEventId.getOrDefault(event.getId(), 0L);
            Long views = viewsByEventId.getOrDefault(event.getId(), 0).longValue();

            return eventMapper.toEventShortDto(
                    event,
                    categoryMapper.toCategoryDto(event.getCategory()),
                    confirmedRequests,
                    initiatorDto,
                    views);
        }).toList();
    }

    @Override
    public EventFullDto getEventByIdForUser(Long eventId, Long userId) {
        log.debug("Getting event for user {}", userId);
        Event event = getUserEventOrThrow(eventId, userId);

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(List.of(eventId));
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(List.of(event));

        Long confirmedRequests = requestsByEventId.getOrDefault(event.getId(), 0L);
        Long views = viewsByEventId.getOrDefault(event.getId(), 0).longValue();

        return buildFullDto(event, views, confirmedRequests);
    }

    @Transactional
    @Override
    public EventFullDto updateEventPrivate(Long eventId,
                                           Long userId,
                                           UpdateEventUserRequestDto dto) {
        log.debug("Updating event with id - {} for user with id - {}", eventId, userId);
        Event event = getUserEventOrThrow(eventId, userId);

        if (!EDITABLE_STATES.contains(event.getState())) {
            throw new ConditionsNotMetException("Only pending or canceled events can be changed");
        }

        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Field: eventDate." +
                        " Error: event date must be at least two hours after the current time. Value: " + dto.getEventDate());
            }
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            event.setCategory(getCategoryOrThrow(dto.getCategory().getId()));
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getLocation() != null) {
            if (dto.getLocation().getLat() == null
                    || dto.getLocation().getLon() == null) {
                throw new ValidationException("Location must contain both latitude and longitude");
            }

            event.setLatitude(dto.getLocation().getLat());
            event.setLongitude(dto.getLocation().getLon());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(List.of(eventId));

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < requestsByEventId.getOrDefault(eventId, 0L)) {
                throw new ConditionsNotMetException("Participant limit cannot be less than confirmed requests");
            }

            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(List.of(event));

        return buildFullDto(
                event,
                viewsByEventId.getOrDefault(event.getId(), 0).longValue(),
                requestsByEventId.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<RequestDto> getRequestsByEventIdAndUserId(Long eventId, Long userId) {
        log.debug("Getting requests for event with id - {}", eventId);
        //Ивент получается не ради id, а для проверки существования и принадлежности юзеру
        Event event = getUserEventOrThrow(eventId, userId);
        List<Request> requests = requestRepository.findByEventId(event.getId());

        return requestMapper.toDto(requests);

    }

    @Transactional
    @Override
    public RequestStatusUpdateResultDto updateRequestStatus(RequestStatusUpdateRequestDto dto,
                                                            Long userId,
                                                            Long eventId) {
        log.debug("update event with id - {} for user with id - {}", eventId, userId);
        Event event = getUserEventOrThrow(eventId, userId);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConditionsNotMetException("Confirmation of participation requests is not required for this event.");
        }

        List<Request> requests = requestRepository.findAllByIdInAndEventId(dto.getRequestIds(), eventId);
        List<Long> requestIds = requests.stream().map(Request::getId).toList();

        dto.getRequestIds().forEach(requestId -> {
            if (!requestIds.contains(requestId)) {
                throw new NotFoundException("Request with id " + requestId + " not found.");
            }
        });

        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConditionsNotMetException(
                        "Request must have status PENDING"
                );
            }
        }

        return switch (dto.getStatus()) {
            case REJECTED -> rejectRequests(requests);
            case CONFIRMED -> confirmRequests(requests, event);
            default -> throw new ValidationException("Status must be CONFIRMED or REJECTED");
        };
    }

    @Override
    public List<EventFullDto> getEventsAdmin(EventAdminSearchParams params) {
        log.debug("get EventAdmin search params - {}", params);
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();

        if (rangeStart != null && rangeEnd != null
                && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Range start must not be after range end.");
        }

        boolean usersEmpty = params.getUsers() == null || params.getUsers().isEmpty();
        boolean statesEmpty = params.getStates() == null || params.getStates().isEmpty();
        boolean categoriesEmpty = params.getCategories() == null || params.getCategories().isEmpty();

        boolean rangeStartNotSet = rangeStart == null;
        boolean rangeEndNotSet = rangeEnd == null;

        List<Event> events = eventRepository.search(
                params.getUsers(),
                usersEmpty,
                params.getStates(),
                statesEmpty,
                params.getCategories(),
                categoriesEmpty,
                rangeStart,
                rangeStartNotSet,
                rangeEnd,
                rangeEndNotSet,
                PageUtils.fromOffset(params.getFrom(), params.getSize())
        );

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(events.stream().map(Event::getId).toList());
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(events);

        return events.stream().map(event -> {
            return buildFullDto(
                    event,
                    viewsByEventId.getOrDefault(event.getId(), 0).longValue(),
                    requestsByEventId.getOrDefault(event.getId(), 0L));
        }).toList();
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequestDto dto) {
        log.debug("update event with id - {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDate = dto.getEventDate() != null ? dto.getEventDate() : event.getEventDate();

        if (eventDate.isBefore(now.plusHours(1))) {
            throw new ValidationException("Event date must be at least one hour after publication.");
        }

        if (dto.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConditionsNotMetException("Only pending events can be published.");
            }
        }

        if (dto.getStateAction() == StateActionAdmin.REJECT_EVENT) {

            if (event.getState() != EventState.PENDING) {
                throw new ConditionsNotMetException("Only pending events can be rejected.");
            }
        }

        if (eventDate != null) {
            event.setEventDate(eventDate);
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            Category category = getCategoryOrThrow(dto.getCategory());
            event.setCategory(category);
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getLocation() != null) {
            if (dto.getLocation().getLat() == null
                    || dto.getLocation().getLon() == null) {
                throw new ValidationException("Location must contain both latitude and longitude");
            }

            event.setLatitude(dto.getLocation().getLat());
            event.setLongitude(dto.getLocation().getLon());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(List.of(eventId));

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < requestsByEventId.getOrDefault(eventId, 0L)) {
                throw new ConditionsNotMetException("Participant limit cannot be less than confirmed requests");
            }

            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(now);
        } else if (dto.getStateAction() == StateActionAdmin.REJECT_EVENT) {
            event.setState(EventState.CANCELED);
        }

        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(List.of(event));

        return buildFullDto(
                event,
                viewsByEventId.getOrDefault(event.getId(), 0).longValue(),
                requestsByEventId.getOrDefault(event.getId(), 0L));

    }

    @Override
    public List<EventShortDto> getEventsPublic(EventPublicSearchParams params,
                                               HttpServletRequest request,
                                               Pageable pageable) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        statsClient.save(EndpointHitDto.builder()
                .app("explore-with-me")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(now)
                .build());

        if (params.getRangeStart() != null && params.getRangeEnd() != null
                && params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new ValidationException("Range start must not be after range end.");
        }

        EventSearchCriteria criteria = new EventSearchCriteria(params);

        if (params.getSort() == SortType.EVENT_DATE) {
            return searchByEventDate(
                    criteria,
                    now,
                    pageable);
        }

        if (params.getSort() == SortType.VIEWS) {
            return searchByViews(
                    criteria,
                    now,
                    pageable);
        }

        return searchWithoutSort(
                criteria,
                now,
                pageable);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        log.debug("get event with id - {}", eventId);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found."));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        statsClient.save(EndpointHitDto.builder()
                .app("explore-with-me")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(now)
                .build());

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(List.of(eventId));
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(List.of(event));

        return buildFullDto(event, viewsByEventId.getOrDefault(
                        eventId, 0).longValue(),
                requestsByEventId.getOrDefault(eventId, 0L));

    }

    @Override
    public List<EventShortDto> getEventsBySubscriptions(Long userId,
                                                        Pageable pageable) {
        User user = getUserOrThrow(userId);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        List<Long> subscriptionIds = subscriptionRepository.findTargetUserIds(user.getId());

        if (subscriptionIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Event> events = eventRepository.findActualEventsByInitiatorIds(subscriptionIds, now, pageable);

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(
                events.stream().map(Event::getId).toList());
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(events);

        return buildShortDtos(events, requestsByEventId, viewsByEventId);
    }

    private List<EventShortDto> searchByEventDate(EventSearchCriteria criteria,
                                                  LocalDateTime now,
                                                  Pageable pageable
    ) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("eventDate").ascending()
        );

        List<Event> events = eventRepository.searchPublicPageable(
                criteria.getText(),
                criteria.getTextEmpty(),
                criteria.getCategories(),
                criteria.getCategoriesEmpty(),
                criteria.getPaid(),
                criteria.getPaidNotSet(),
                criteria.getRangeStart(),
                criteria.getRangeStartNotSet(),
                criteria.getRangeEnd(),
                criteria.getRangeEndNotSet(),
                criteria.getOnlyAvailable(),
                now,
                pageable
        );

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(
                events.stream().map(Event::getId).toList());
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(events);

        return buildShortDtos(events, requestsByEventId, viewsByEventId);
    }

    private List<EventShortDto> searchByViews(EventSearchCriteria criteria,
                                              LocalDateTime now,
                                              Pageable pageable) {

        List<Event> events = eventRepository.searchPublic(
                criteria.getText(),
                criteria.getTextEmpty(),
                criteria.getCategories(),
                criteria.getCategoriesEmpty(),
                criteria.getPaid(),
                criteria.getPaidNotSet(),
                criteria.getRangeStart(),
                criteria.getRangeStartNotSet(),
                criteria.getRangeEnd(),
                criteria.getRangeEndNotSet(),
                criteria.getOnlyAvailable(),
                now
        );

        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(events);
        events.sort(Comparator.comparing(
                (Event event) -> viewsByEventId.getOrDefault(event.getId(), 0)
        ).reversed());
        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(
                events.stream().map(Event::getId).toList());

        return buildShortDtos(events.stream()
                        .skip(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .toList(),
                requestsByEventId,
                viewsByEventId);
    }

    private List<EventShortDto> searchWithoutSort(EventSearchCriteria criteria,
                                                  LocalDateTime now,
                                                  Pageable pageable) {

        List<Event> events = eventRepository.searchPublicPageable(
                criteria.getText(),
                criteria.getTextEmpty(),
                criteria.getCategories(),
                criteria.getCategoriesEmpty(),
                criteria.getPaid(),
                criteria.getPaidNotSet(),
                criteria.getRangeStart(),
                criteria.getRangeStartNotSet(),
                criteria.getRangeEnd(),
                criteria.getRangeEndNotSet(),
                criteria.getOnlyAvailable(),
                now,
                pageable
        );

        Map<Long, Long> requestsByEventId = eventStatsService.getConfirmedRequestsMap(
                events.stream().map(Event::getId).toList());
        Map<Long, Integer> viewsByEventId = eventStatsService.getViewsMap(events);

        return buildShortDtos(events, requestsByEventId, viewsByEventId);
    }

    private RequestStatusUpdateResultDto confirmRequests(List<Request> requests, Event event) {
        List<RequestDto> rejectedRequests = new ArrayList<>();
        List<RequestDto> confirmedRequests = new ArrayList<>();

        List<RequestCountProjection> requestCountProjections = requestRepository
                .getRequestCountsByEventIds(List.of(event.getId()), RequestStatus.CONFIRMED);

        long freePlaces = event.getParticipantLimit();

        if (!requestCountProjections.isEmpty()) {
            freePlaces = freePlaces - requestCountProjections.get(0).getRequestCount();
        }

        if (freePlaces < 1) {
            throw new ConflictException("The participant limit has been reached");
        }

        for (Request request : requests) {
            if (freePlaces > 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.toDto(request));
                freePlaces--;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(request));
            }
        }

        return RequestStatusUpdateResultDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private RequestStatusUpdateResultDto rejectRequests(List<Request> requests) {

        List<RequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requests) {
            request.setStatus(RequestStatus.REJECTED);
            rejectedRequests.add(requestMapper.toDto(request));
        }

        return RequestStatusUpdateResultDto.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private EventFullDto buildFullDto(
            Event event,
            Long views,
            Long confirmedRequests
    ) {
        return eventMapper.toFullEventDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                userMapper.toUserShortDto(event.getInitiator()),
                views,
                confirmedRequests,
                toLocationDto(event)
        );
    }

    private List<EventShortDto> buildShortDtos(
            List<Event> events,
            Map<Long, Long> requestsByEventId,
            Map<Long, Integer> viewsByEventId) {

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        requestsByEventId.getOrDefault(event.getId(), 0L),
                        userMapper.toUserShortDto(event.getInitiator()),
                        viewsByEventId.getOrDefault(event.getId(), 0).longValue()
                ))
                .toList();
    }

    private LocationDto toLocationDto(Event event) {
        return new LocationDto(
                event.getLatitude(),
                event.getLongitude()
        );
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getUserEventOrThrow(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }
}
