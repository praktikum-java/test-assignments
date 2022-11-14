package ru.practicum.categories.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.service.admin.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin.Категории", description = "API для работы с категориями. Только для администраторов.")
public class CategoryAdminController {
    private final CategoryAdminService adminService;

    @PatchMapping
    @Operation(summary = "Изменение категории",
            description = "Возвращает CategoryDto. Имя категории должно быть уникальным.")
    public CategoryDto changeCategory(@Valid @RequestBody @Parameter(description = "Входящее DTO категории")
                                      CategoryDto categoryDto) {
        log.info("Received a request: PATCH /admin/categories with body: " + categoryDto);
        return adminService.changeCategory(categoryDto);
    }

    @PostMapping
    @Operation(summary = "Добавление новой категории",
            description = "Возвращает CategoryDto. Имя категории должно быть уникальным.")
    public CategoryDto createCategory(@Valid @RequestBody @Parameter(description = "DTO для создания новой категории")
                                      NewCategoryDto newCategoryDto) {
        log.info("Received a request: POST /admin/categories with body: " + newCategoryDto);
        return adminService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @Operation(summary = "Удаление категории",
            description = "Возвращает только статус выполнения или ошибку. " +
                    "С категорией не должно быть связано ни одного события.")
    public void removeCategory(@Positive @PathVariable @Parameter(name = "Id события для удаления")
                               Long catId) {
        log.info("Received a request: DELETE /admin/categories/{}", catId);
        adminService.removeCategory(catId);
    }
}
