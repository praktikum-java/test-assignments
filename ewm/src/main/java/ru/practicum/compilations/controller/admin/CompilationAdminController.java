package ru.practicum.compilations.controller.admin;

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
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.service.admin.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin.Подборки событий", description = "API для работы с подборками событий. Только для администраторов.")
public class CompilationAdminController {
    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @Operation(summary = "Добавление новой подборки", description = "Возвращает CompilationDto")
    public CompilationDto createNewCompilation(@Valid @RequestBody @Parameter(name = "DTO для создания нового события")
                                               NewCompilationDto newCompilationDto) {
        log.info("Received a request POST /admin/compilations with body: {}", newCompilationDto);
        return compilationAdminService.createNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @Operation(summary = "Удаление подборки по id", description = "Возвращает только статус ответа или ошибку")
    public void removeCompilation(@Positive @PathVariable Long compId) {
        log.info("Received a request DELETE /admin/compilations/{}", compId);
        compilationAdminService.removeCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    @Operation(summary = "Удалить событие из подборки", description = "Возвращает только статус ответа или ошибку.")
    public void removeEventFromCompilation(@Positive @PathVariable
                                           @Parameter(name = "id подборки, из которой нужно удалить")
                                           Long compId,
                                           @Positive @PathVariable
                                           @Parameter(name = "id события, которое нужно удалить из подборки")
                                           Long eventId) {
        log.info("Received a request DELETE /admin/compilations/{}/events/{}", compId, eventId);
        compilationAdminService.removeEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    @Operation(summary = "Добавить событие в подборку", description = "Возвращает только статус ответа или ошибку")
    public void addEventInCompilation(@Positive @PathVariable
                                      @Parameter(name = "id подборки, в которую нужно добавить событие")
                                      Long compId,
                                      @Positive @PathVariable
                                      @Parameter(name = "id события, которое нужно добавить в подборку")
                                      Long eventId) {
        log.info("Received a request PATCH /admin/compilations/{}/events/{}", compId, eventId);
        compilationAdminService.addEventInCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    @Operation(summary = "Открепить подборку на главной странице", description = "Возвращает только статус ответа или ошибку")
    public void unpinCompilation(@Positive @PathVariable Long compId) {
        log.info("Received a request DELETE /admin/compilations/{}/pin", compId);
        compilationAdminService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    @Operation(summary = "Закрепить подборку на главной странице", description = "Возвращает только статус ответа или ошибку")
    public void pinCompilation(@Positive @PathVariable Long compId) {
        log.info("Received a request PATCH /admin/compilations/{}/pin", compId);
        compilationAdminService.pinCompilation(compId);
    }
}
