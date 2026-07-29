package ru.practicum.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.HitMapper;
import ru.practicum.server.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class HitServiceImpl implements HitService {
    private final StatsRepository repository;
    private final HitMapper mapper;

    @Override
    public void save(EndpointHitDto hitDto) {
        log.debug("save hit: {}", hitDto);
        repository.save(mapper.toHit(hitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        log.debug("getStats({}, {}, {}, {})", start, end, uris, unique);

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end cannot be before start");
        }

        if (unique) {
            return mapper.toViewStatsDtoList(repository.getStatsUniqueIp(start, end, uris));
        } else {
            return mapper.toViewStatsDtoList(repository.getStats(start, end, uris));
        }

    }
}
