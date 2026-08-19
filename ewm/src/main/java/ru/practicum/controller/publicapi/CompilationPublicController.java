package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.util.PageUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return compilationService.getCompilations(pinned, PageUtils.fromOffset(from, size));
    }
}