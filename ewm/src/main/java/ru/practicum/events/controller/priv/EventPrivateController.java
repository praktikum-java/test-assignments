package ru.practicum.events.controller.priv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.events.dto.priv.NewEventDto;
import ru.practicum.events.dto.priv.UpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.events.service.priv.EventPrivateService;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Private.События", description = "Закрытый API для работы с событиями.")
public class EventPrivateController {
    private final EventPrivateService eventPrivateService;

    @GetMapping("/{userId}/events")
    @Operation(summary = "Получение событий, добавленных текущим пользователем", description = "Возвращает список EventShortDto")
    public List<EventShortDto> getEventsByUserId(@Positive @PathVariable Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received a request: GET /users/{}/events with parameters: from = {}, size = {}", userId, from, size);
        return eventPrivateService.getEventsByUserId(userId, from, size);
    }

    @PatchMapping("/{userId}/events")
    @Operation(summary = "Изменение события, добавленного текущим пользователем",
            description = "Изменить можно только отмененные события или события в состоянии ожидания модерации. " +
                    "Если редактируется отменённое событие, то оно автоматически переходит в состояние ожидания модерации. " +
                    "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента.")
    public EventFullDto changeEventByUser(@Positive @PathVariable Long userId,
                                          @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Received a request: PATCH /users/{}/events with body: {}", userId, updateEventRequest);
        return eventPrivateService.changeEventByUser(userId, updateEventRequest);
    }

    @PostMapping("/{userId}/events")
    @Operation(summary = "Добавление нового события", description = "Возвращает EventFullDto. " +
            "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента")
    public EventFullDto createEvent(@Positive @PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Received a request: POST /users/{}/events with body: {}", userId, newEventDto);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("The start of the event cannot be earlier than 2 hours later");
        }
        return eventPrivateService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @Operation(summary = "Получение полной информации о событии, добавленном текущим пользователем",
            description = "Возвращает EventFullDto")
    public EventFullDto getEventById(@Positive @PathVariable Long userId,
                                     @Positive @PathVariable Long eventId) {
        log.info("Received a request: GET /users/{}/events/{} ", userId, eventId);
        return eventPrivateService.getEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @Operation(summary = "Отмена события добавленного текущим пользователем", description = "Возвращает EventFullDto. " +
            "Обратите внимание: Отменить можно только событие в состоянии ожидания модерации.")
    public EventFullDto cancellationEvent(@Positive @PathVariable Long userId,
                                          @Positive @PathVariable Long eventId) {
        log.info("Received a request: PATCH /users/{}/events/{} ", userId, eventId);
        return eventPrivateService.cancellationEvent(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @Operation(summary = "Получение информации о запросах на участие в событии текущего пользователя",
            description = "Возвращает ParticipationRequestDto.")
    public List<ParticipationRequestDto> getParticipationRequest(@Positive @PathVariable Long userId,
                                                                 @Positive @PathVariable Long eventId) {
        log.info("Received a request: GET /users/{}/events/{}/requests ", userId, eventId);
        return eventPrivateService.getParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    @Operation(summary = "Подтверждение чужой заявки на участие в событии текущего пользователя",
            description = "Если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение " +
                    "заявок не требуется. Если при подтверждении данной заявки, лимит заявок для события исчерпан, " +
                    "то все неподтверждённые заявки необходимо отклонить")

    public ParticipationRequestDto acceptParticipationRequest(@Positive @PathVariable Long userId,
                                                              @Positive @PathVariable Long eventId,
                                                              @Positive @PathVariable Long reqId) {
        log.info("Received a request: PATCH /users/{}/events/{}/requests/{}/confirm", userId, eventId, reqId);
        return eventPrivateService.acceptParticipationRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    @Operation(summary = "Отклонение чужой заявки на участие в событии текущего пользователя",
            description = "Возвращает ParticipationRequestDto")
    public ParticipationRequestDto rejectParticipationRequest(@Positive @PathVariable Long userId,
                                                              @Positive @PathVariable Long eventId,
                                                              @Positive @PathVariable Long reqId) {
        log.info("Received a request: PATCH /users/{}/events/{}/requests/{}/reject", userId, eventId, reqId);
        return eventPrivateService.rejectParticipationRequest(userId, eventId, reqId);
    }
}
