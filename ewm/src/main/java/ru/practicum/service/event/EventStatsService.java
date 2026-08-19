package ru.practicum.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Event;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.RequestCountProjection;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventStatsService {

    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return requestRepository.getRequestCountsByEventIds(eventIds, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(
                        RequestCountProjection::getEventId,
                        RequestCountProjection::getRequestCount
                ));
    }

    public Map<Long, Integer> getViewsMap(List<Event> events) {
        LocalDateTime minPublishDate = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (minPublishDate == null) {
            return Collections.emptyMap();
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(
                minPublishDate,
                LocalDateTime.now(),
                uris,
                true
        );

        return viewStatsDtos.stream()
                .collect(Collectors.toMap(
                        stat -> Long.parseLong(
                                stat.getUri().substring("/events/".length())
                        ),
                        ViewStatsDto::getHits
                ));
    }
}