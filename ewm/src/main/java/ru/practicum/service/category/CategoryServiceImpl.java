package ru.practicum.service.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CreateCategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(CreateCategoryDto createCategoryDto) {
        log.debug("Adding category {}", createCategoryDto);
        return categoryMapper.toCategoryDto(
                categoryRepository.save(
                        categoryMapper.toCategory(createCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.debug("Updating category {}", categoryDto);
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryDto.getId() + " was not found"));
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Deleting category {}", id);
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
    }

    @Override
    public CategoryDto getCategory(Long id) {
        log.debug("Retrieving category {}", id);
        return categoryMapper.toCategoryDto(categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found")));
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        log.debug("Retrieving categories");
        return categoryMapper.toCategoryDto(
                categoryRepository.findAll(pageable).getContent());
    }

}
