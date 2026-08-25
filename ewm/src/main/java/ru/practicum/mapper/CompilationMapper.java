package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CreateCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Map;

@Component
public class CompilationMapper {

    public CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }

    public Compilation toCompilation(CreateCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .events(events)
                .build();
    }

    public List<CompilationDto> toDto(List<Compilation> compilations,
                                      Map<Long, List<EventShortDto>> eventsByCompilationId) {
        return compilations.stream().map(compilation -> {
            return CompilationDto.builder()
                    .events(eventsByCompilationId.get(compilation.getId()))
                    .id(compilation.getId())
                    .title(compilation.getTitle())
                    .pinned(compilation.getPinned())
                    .build();
        }).toList();
    }

}
