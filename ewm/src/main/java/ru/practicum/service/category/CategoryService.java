package ru.practicum.service.category;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CreateCategoryDto;

import java.util.List;

public interface CategoryService {
    @Transactional
    CategoryDto addCategory(CreateCategoryDto createCategoryDto);

    @Transactional
    CategoryDto updateCategory(CategoryDto categoryDto);

    @Transactional
    void deleteCategory(Long id);

    CategoryDto getCategory(Long id);

    List<CategoryDto> getCategories(Pageable pageable);
}
