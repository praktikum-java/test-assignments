package ru.practicum.categories.controller.publ;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.publ.CategoryPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Public.Категории", description = "Публичный API для работы с категориями.")
public class CategoryPublicController {
    private final CategoryPublicService categoryPublicService;

    @GetMapping
    @Operation(summary = "Получение категорий", description = "Возвращает список CategoryDto")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received a request: GET /categories with parameters: from = {}, size = {}", from, size);
        return categoryPublicService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @Operation(summary = "Получение информации о категории по ее id", description = "Возвращает CategoryDto")
    public CategoryDto getCategoryById(@Positive @PathVariable Long catId) {
        log.info("Received a request: GET /categories/{}", catId);
        return categoryPublicService.getCategoryById(catId);
    }
}
