package ru.practicum.events.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.events.dto.admin.AdminUpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.model.EventState;
import ru.practicum.events.service.admin.EventAdminService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin.События", description = "API для работы с событиями. Только для администраторов.")
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @GetMapping
    @Operation(summary = "Поиск событий", description = "Возвращает list of EventFullDto. Эндпоинт возвращает полную " +
            "информацию обо всех событиях подходящих под переданные условия")
    public List<EventFullDto> getAllEvents(@Parameter(name = "список id пользователей, чьи события нужно найти")
                                           @RequestParam(required = false) List<Long> users,
                                           @Parameter(name = "список состояний в которых находятся искомые события")
                                           @RequestParam(required = false) List<EventState> states,
                                           @Parameter(name = "список id категорий в которых будет вестись поиск")
                                           @RequestParam(required = false) List<Long> categories,
                                           @Parameter(name = "дата и время не раньше которых должно произойти событие")
                                           @RequestParam(required = false) LocalDateTime rangeStart,
                                           @Parameter(name = "дата и время не позже которых должно произойти событие")
                                           @RequestParam(required = false) LocalDateTime rangeEnd,
                                           @Parameter(name = "количество событий, которые нужно пропустить для формирования текущего набора")
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Parameter(name = "количество событий в наборе")
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received a request: GET /admin/events with parameters: users = {}, states = {}, categories = {}, " +
                        "rangeStart = {}, rangeEnd = {}, from = {}, size = {}", users,
                states, categories, rangeStart, rangeEnd, from, size);
        return eventAdminService.getAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    @Operation(summary = "Редактирование событий", description = "Возвращает EventFullDto. Редактирование данных " +
            "любого события администратором. Валидация данных не требуется.")
    public EventFullDto changeEvent(@Positive @PathVariable Long eventId,
                                    @Parameter(name = "DTO для изменения события администратором")
                                    @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("Received a request: PUT /admin/events/{} with body: {}", eventId, adminUpdateEventRequest);
        return eventAdminService.changeEvent(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    @Operation(summary = "Публикация события", description = "Возвращает EventFullDto. Дата начала события должна быть " +
            "не ранее чем за час от даты публикации. Событие должно быть в состоянии ожидания публикации")
    public EventFullDto publishingEvent(@Positive @PathVariable Long eventId) {
        log.info("Received a request: PATCH /admin/events/{}/publish ", eventId);
        return eventAdminService.publishingEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    @Operation(summary = "Отклонение события", description = "Возвращает EventFullDto. Отклонение возможно только, если " +
            "событие не опубликовано.")
    public EventFullDto rejectEvent(@Positive @PathVariable Long eventId) {
        log.info("Received a request: PATCH /admin/events/{}/reject ", eventId);
        return eventAdminService.rejectEvent(eventId);
    }
}
