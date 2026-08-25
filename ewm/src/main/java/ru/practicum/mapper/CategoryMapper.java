package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CreateCategoryDto;
import ru.practicum.model.Category;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toCategory(CreateCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public List<CategoryDto> toCategoryDto(List<Category> categories) {
        return categories.stream().map(this::toCategoryDto).toList();
    }

}
