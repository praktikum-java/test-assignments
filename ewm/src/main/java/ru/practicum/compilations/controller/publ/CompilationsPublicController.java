package ru.practicum.compilations.controller.publ;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.publ.CompilationPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Public.Подборки событий", description = "Публичный API для работы с подборками событий.")
public class CompilationsPublicController {
    private final CompilationPublicService compilationPublicService;

    @GetMapping
    @Operation(summary = "Получение подборок событий", description = "Возвращает список CompilationDto")
    public List<CompilationDto> getCompilations(@RequestParam(required = false)
                                                @Parameter(name = "Искать только закрепленные/не закрепленные подборки")
                                                Boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(defaultValue = "10")
                                                Integer size) {
        log.info("Received a request GET /compilations");
        return compilationPublicService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @Operation(summary = "Получение подборки событий по id", description = "Возвращает CompilationDto")
    public CompilationDto getCompilationById(@Positive @PathVariable Long compId) {
        log.info("Received a request GET /compilations/{}", compId);
        return compilationPublicService.getCompilationById(compId);
    }
}
