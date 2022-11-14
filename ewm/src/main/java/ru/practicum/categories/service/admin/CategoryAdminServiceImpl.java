package ru.practicum.categories.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = false)
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new CustomNotFoundException("Category not found"));
        categoryMapper.updateCategory(categoryDto, category);
        categoryRepository.save(category);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = false)
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toCategory(newCategoryDto);
        Category readyCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(readyCategory);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeCategory(Long catId) {
        Long relatedEvents = eventRepository.countByCategoryId(catId);
        if (relatedEvents != 0) {
            throw new ValidationException("You cannot delete a category that events are associated with");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getEntityCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new CustomNotFoundException("Category not found"));
    }
}
