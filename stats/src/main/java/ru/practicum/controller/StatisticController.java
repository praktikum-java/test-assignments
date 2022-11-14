package ru.practicum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatisticService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin.Контроллер статистики", description = "API для работы со статистикой посещений")
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    @Operation(summary = "Сохранение информации о том, что к эндпоинту был запрос",
            description = "Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем." +
                    " Название сервиса, uri и ip пользователя указаны в теле запроса.")
    public void save(@NotNull @RequestBody
                     @Parameter(name = "Объект, который хранит информацию о запросе")
                     EndpointHit endpointHit) {
        log.info("Received a request: POST /hit with body: {}", endpointHit);
        statisticService.save(endpointHit);
    }

    @GetMapping("/stats")
    @Operation(summary = "Получение статистики по посещениям", description = "Возвращает список ViewStats. " +
            "Значение даты и времени должно быть закодировано (Например, используя java.net.URLEncoder.encode)")
    public List<ViewStats> getStatistic(
            @Parameter(name = "Дата и время начала диапазона за который нужно выгрузить статистику (в формате yyyy-MM-dd HH:mm:ss)")
            @RequestParam LocalDateTime start,
            @Parameter(name = "Дата и время конца диапазона за который нужно выгрузить статистику (в формате yyyy-MM-dd HH:mm:ss)")
            @RequestParam LocalDateTime end,
            @Parameter(name = "Список uri для которых нужно выгрузить статистику")
            @RequestParam(required = false) String[] uris,
            @Parameter(name = "Нужно ли учитывать только уникальные посещения (только с уникальным ip)")
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Received a request: GET /stats with parameters: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return statisticService.getStats(start, end, uris, unique);
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "Получение информации о количестве просмотров события")
    public Long getViews(@NotNull(message = "Controller get request to path '/events/{eventId}' where eventId==null")
                            @PathVariable Long eventId) {
        log.info("Received a request: GET /events with pathVariable: {}", eventId);
        return statisticService.getViews("/events/" + eventId);
    }
}