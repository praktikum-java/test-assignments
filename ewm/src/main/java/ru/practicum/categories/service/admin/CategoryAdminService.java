package ru.practicum.categories.service.admin;

import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.entity.Category;

@Service
public interface CategoryAdminService {
    CategoryDto changeCategory(CategoryDto categoryDto);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void removeCategory(Long catId);

    Category getEntityCategoryById(Long catId);
}
