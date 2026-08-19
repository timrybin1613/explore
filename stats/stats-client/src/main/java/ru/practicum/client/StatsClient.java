package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    private static final String HIT_PATH = "/hit";
    private static final String VIEW_STATS_PATH = "/stats";

    public StatsClient(
            RestTemplate restTemplate,
            @Value("${stats.server.url}") String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public void save(EndpointHitDto dto) {
        try {
            restTemplate.postForEntity(
                    serverUrl + HIT_PATH,
                    dto,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            log.error(
                    "Stats server returned {}: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
        } catch (RestClientException e) {
            log.error("Не удалось сохранить статистику", e);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       Boolean unique) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + VIEW_STATS_PATH)
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", uris.toArray());
        }

        URI uri = builder.build().toUri();

        ResponseEntity<List<ViewStatsDto>> response =
                restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        return Optional.ofNullable(response.getBody())
                .orElse(Collections.emptyList());
    }
}
