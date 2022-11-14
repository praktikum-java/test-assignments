package ru.practicum.categories.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapperImpl;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.categories.service.publ.CategoryPublicServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryPublicServiceTest {
    @InjectMocks
    private CategoryPublicServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Spy
    private CategoryMapperImpl categoryMapper;
    Category category1 = Category.builder().id(1L).name("Sport").build();
    Category category2 = Category.builder().id(2L).name("ArtHouse").build();
    CategoryDto categoryDto1 = CategoryDto.builder().id(1L).name("Sport").build();
    CategoryDto categoryDto2 = CategoryDto.builder().id(2L).name("ArtHouse").build();
    Integer from = 0;
    Integer size = 10;
    Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
    Page<Category> page = new PageImpl<>(List.of(category1, category2));

    // getCategories
    @Test
    public void shouldGetListCategoryDto() {
        when(categoryRepository.findAll(pageable)).thenReturn(page);
        List<CategoryDto> result = categoryService.getCategories(0, 10);
        assertEquals(List.of(categoryDto1, categoryDto2), result);
    }

    // getCategoryById
    @Test
    public void shouldGetCategoryDtoById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        CategoryDto result = categoryService.getCategoryById(1L);
        assertEquals(categoryDto1, result);
    }
}
