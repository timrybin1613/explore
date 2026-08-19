package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CreateCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.service.compilation.CompilationService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody CreateCompilationDto dto) {
        return compilationService.createCompilation(dto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequestDto dto) {
        return compilationService.updateCompilation(compId, dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }
}
