package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CreateCategoryDto;
import ru.practicum.service.category.CategoryService;

@Validated
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto addCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(catId);
        return categoryService.updateCategory(categoryDto);
    }

}
