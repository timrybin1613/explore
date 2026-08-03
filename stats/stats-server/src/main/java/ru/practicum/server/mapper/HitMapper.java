package ru.practicum.server.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.Hit;
import ru.practicum.server.storage.ViewStatsProjection;

import java.util.ArrayList;
import java.util.List;

@Component
public class HitMapper {

    public Hit toHit(EndpointHitDto dto) {
        return Hit.builder()
                .id(dto.getId())
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public ViewStatsDto toViewStatsDto(ViewStatsProjection projection) {
        return ViewStatsDto.builder()
                .app(projection.getApp())
                .uri(projection.getUri())
                .hits(projection.getHits())
                .build();
    }

    public List<ViewStatsDto> toViewStatsDtoList(List<ViewStatsProjection> projections) {
        List<ViewStatsDto> viewStatsDtos = new ArrayList<>();
        for (ViewStatsProjection projection : projections) {
            viewStatsDtos.add(toViewStatsDto(projection));
        }
        return viewStatsDtos;
    }
}
