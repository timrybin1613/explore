package ru.practicum.service.compilation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CreateCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.event.EventStatsService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventStatsService eventStatsService;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationDto createCompilation(CreateCompilationDto dto) {
        log.debug("Creating compilation {}", dto.getTitle());

        List<Event> events = dto.getEvents() == null
                ? Collections.emptyList()
                : eventRepository.findAllById(dto.getEvents());

        if (dto.getPinned() == null) {
            dto.setPinned(false);
        }

        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(dto, events));

        return compilationMapper.toDto(compilation, buildEventShortDtos(events));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compilationId) {
        log.debug("Deleting compilation {}", compilationId);
        compilationRepository.delete(getCompilationOrThrow(compilationId));
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequestDto dto) {
        log.debug("Updating compilation {}", compilationId);

        Compilation compilation = getCompilationOrThrow(compilationId);

        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(events);
        }

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        return compilationMapper.toDto(compilation, buildEventShortDtos(compilation.getEvents()));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        log.debug("Getting compilations for pinned {}", pinned);
        List<Compilation> compilations = pinned == null ? compilationRepository.findAll(pageable).getContent()
                : compilationRepository.findAllByPinned(pinned, pageable);

        Map<Long, List<EventShortDto>> eventsByCompilationId = compilations.stream()
                .collect(Collectors.toMap(
                        Compilation::getId,
                        compilation -> buildEventShortDtos(compilation.getEvents())
                ));

        return compilationMapper.toDto(compilations, eventsByCompilationId);

    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        log.debug("Getting compilation {}", compilationId);
        Compilation compilation = getCompilationOrThrow(compilationId);

        return compilationMapper.toDto(compilation, buildEventShortDtos(compilation.getEvents()));
    }

    private Compilation getCompilationOrThrow(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compilationId + " not found"));
    }

    private List<EventShortDto> buildEventShortDtos(List<Event> events) {

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> confirmedRequests =
                eventStatsService.getConfirmedRequestsMap(
                        events.stream().map(Event::getId).toList());

        Map<Long, Integer> views = eventStatsService.getViewsMap(events);

        return events.stream().map(event -> {
            return eventMapper.toEventShortDto(
                    event,
                    categoryMapper.toCategoryDto(event.getCategory()),
                    confirmedRequests.getOrDefault(event.getId(), 0L),
                    userMapper.toUserShortDto(event.getInitiator()),
                    views.getOrDefault(event.getId(), 0).longValue()
            );
        }).toList();
    }
}
