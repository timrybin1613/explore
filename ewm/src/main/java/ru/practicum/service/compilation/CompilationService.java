package ru.practicum.service.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CreateCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {
    @Transactional
    CompilationDto createCompilation(CreateCompilationDto dto);

    @Transactional
    void deleteCompilation(Long compilationId);

    @Transactional
    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequestDto dto);

    List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

    CompilationDto getCompilationById(Long compilationId);
}
