package ru.practicum.categories.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapperImpl;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.categories.service.admin.CategoryAdminServiceImpl;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryAdminServiceTest {
    @InjectMocks
    private CategoryAdminServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private CategoryMapperImpl categoryMapper;

    Category category1 = Category.builder().id(1L).name("Sport").build();
    CategoryDto categoryDto1 = CategoryDto.builder().id(1L).name("Sport").build();

    // changeCategory
    @Test
    public void shouldChangeCategoryAndReturnCategoryDto() {
        CategoryDto categoryDto0 = CategoryDto.builder().id(1L).name("Sport games").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(category1)).thenReturn(category1);
        CategoryDto result = categoryService.changeCategory(categoryDto0);
        assertEquals(categoryDto0, result);
    }

    @Test
    public void shouldTryChangeCategoryAndThrowException() {
        CategoryDto categoryDto0 = CategoryDto.builder().id(1L).name("Sport games").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> categoryService.changeCategory(categoryDto0));
        assertEquals("Category not found", re.getMessage());
    }

    // createCategory
    @Test
    public void shouldCreateCategoryAndReturnDto() {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("Sport").build();
        Category category0 = Category.builder().name("Sport").build();
        when(categoryRepository.save(category0)).thenReturn(category1);
        CategoryDto result = categoryService.createCategory(newCategoryDto);
        CategoryDto categoryToCheck = categoryDto1;
        assertEquals(categoryToCheck, result);
    }

    // removeCategory
    @Test
    public void shouldCallRepositoryAndRemoveCategory() {
        when(eventRepository.countByCategoryId(1L)).thenReturn(0L);
        categoryService.removeCategory(1L);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    public void shouldTryRemoveCategoryAndThrowException() {
        when(eventRepository.countByCategoryId(1L)).thenReturn(1L);
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> categoryService.removeCategory(1L));
        assertEquals("You cannot delete a category that events are associated with", re.getMessage());
        Mockito.verify(categoryRepository, Mockito.times(0))
                .deleteById(1L);
    }

    // getEntityCategoryById
    @Test
    public void shouldGetCategoryEntity() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        Category result = categoryService.getEntityCategoryById(1L);
        assertEquals(category1, result);
    }

    @Test
    public void shouldTryGetCategoryEntityAndThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> categoryService.getEntityCategoryById(1L));
        assertEquals("Category not found", re.getMessage());
    }
}
