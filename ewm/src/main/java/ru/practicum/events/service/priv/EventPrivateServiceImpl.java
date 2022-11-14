package ru.practicum.events.service.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.service.admin.CategoryAdminService;
import ru.practicum.client.StatisticClient;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.dto.priv.NewEventDto;
import ru.practicum.events.dto.priv.UpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.entity.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.entity.User;
import ru.practicum.users.service.UserService;
import ru.practicum.util.UtilCollectorsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final CategoryAdminService categoryAdminService;
    private final StatisticClient statisticClient;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final RequestMapper requestMapper;

    // Получение событий, добавленных текущим пользователем.
    @Override
    @Transactional
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        User initiator = userService.getEntityUserById(userId);
        List<Event> events = eventRepository.findByInitiator(initiator, pageable);
        return UtilCollectorsDto.getListEventShortDto(events, statisticClient, eventMapper, requestRepository);
    }

    // Изменение события, добавленного текущим пользователем.
    /* Изменять можно только отмененные события или события в состоянии ожидания модерации
     * Если редактируется отменённое событие, то оно автоматически переходит в состояние ожидания модерации
     * Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента */
    @Override
    @Transactional(readOnly = false)
    public EventFullDto changeEventByUser(Long userId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findById(updateEventRequest.getEventId())
                .orElseThrow(() -> new CustomNotFoundException("Event not found"));
        checkInitiatorId(event, userId);
        EventState stateOfEvent = event.getState();
        if (Boolean.FALSE.equals(stateOfEvent.equals(EventState.PENDING))
                && Boolean.FALSE.equals(stateOfEvent.equals(EventState.CANCELED))) {
            throw new ValidationException("You cannot edit an event that is not in the PENDING or CANCELED status");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("The start of the event cannot be earlier than 2 hours later");
        }
        if (event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }
        Category category = null;
        if (updateEventRequest.getCategory() != null) {
            category = categoryAdminService.getEntityCategoryById(updateEventRequest.getCategory());
        }
        eventMapper.updateEventFromUpdateEventRequest(updateEventRequest, event, category);
        Event readyEvent = eventRepository.save(event);
        return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                statisticClient, eventMapper, requestRepository);
    }

    // Добавление нового события.
    /* !!! Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента*/
    @Override
    @Transactional(readOnly = false)
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User initiator = userService.getEntityUserById(userId);
        Category category = categoryAdminService.getEntityCategoryById(newEventDto.getCategory());
        Event event = eventMapper.toEventFromNewEventDto(newEventDto, category, initiator, EventState.PENDING, createdOn,
                null, null, true);
        Event readyEvent = eventRepository.save(event);
        return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                null, eventMapper, requestRepository);
    }

    // Получение полной информации о событии, добавленном текущим пользователем.
    @Override
    @Transactional
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new CustomNotFoundException("Event not found"));
        checkInitiatorId(event, userId);
        return UtilCollectorsDto.getEventFullDto(event, categoryMapper, userMapper,
                statisticClient, eventMapper, requestRepository);
    }

    // Отмена события добавленного текущим пользователем.
    /* Обратите внимание: Отменить можно только событие в состоянии ожидания модерации.*/
    @Override
    @Transactional(readOnly = false)
    public EventFullDto cancellationEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new CustomNotFoundException("Event not found"));
        checkInitiatorId(event, userId);
        if (Boolean.FALSE.equals(event.getState().equals(EventState.PENDING))) {
            throw new ValidationException("Cannot cancel an event that is not PENDING");
        }
        event.setState(EventState.CANCELED);
        Event readyEvent = eventRepository.save(event);
        return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                statisticClient, eventMapper, requestRepository);
    }

    // Получение информации о запросах на участие в событии текущего пользователя.
    @Override
    @Transactional
    public List<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId) {
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(e -> requestMapper.toRequestDto(e, e.getEvent().getId(), e.getRequester().getId()))
                .collect(Collectors.toList());
    }

    // Подтверждение чужой заявки на участие в событии текущего пользователя.
    /* Если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
     * Если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки
     * необходимо отклонить */
    @Override
    @Transactional(readOnly = false)
    public ParticipationRequestDto acceptParticipationRequest(Long userId, Long eventId, Long reqId) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new CustomNotFoundException("Request not found"));
        checkParametersOfRequest(request, userId, eventId);
        if (request.getEvent().getRequestModeration().equals(false) || request.getEvent().getParticipantLimit().equals(0L)) {
            throw new ValidationException("Confirmation of requests for this event is not required");
        }
        request.setStatus(RequestStatus.CONFIRMED);
        Request readyRequest = requestRepository.save(request);
        /* Если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки
        необходимо отклонить*/
        Event event = readyRequest.getEvent();
        if (event.getParticipantLimit() != 0) {
            Long participantLimit = event.getParticipantLimit();
            Long confirmedRequests = requestRepository
                    .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).orElse(0L);
            if (participantLimit.equals(confirmedRequests)) {
                List<Request> cancellationRequests = requestRepository
                        .findAllByEventIdAndStatus(event.getId(), RequestStatus.PENDING);
                for (Request req : cancellationRequests) {
                    req.setStatus(RequestStatus.CANCELED);
                    requestRepository.save(req);
                }
                event.setAvailableForRequest(false);
                eventRepository.save(event);
            }
        }
        return requestMapper.toRequestDto(readyRequest, readyRequest.getEvent().getId(),
                readyRequest.getRequester().getId());
    }

    // Отклонение чужой заявки на участие в событии текущего пользователя.
    @Override
    @Transactional(readOnly = false)
    public ParticipationRequestDto rejectParticipationRequest(Long userId, Long eventId, Long reqId) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new CustomNotFoundException("Request not found"));
        checkParametersOfRequest(request, userId, eventId);
        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        Request readyRequest = requestRepository.save(request);
        if (oldStatus.equals(RequestStatus.CONFIRMED)) {
            Event event = request.getEvent();
            event.setAvailableForRequest(true);
            eventRepository.save(event);
        }
        return requestMapper.toRequestDto(readyRequest, readyRequest.getEvent().getId(),
                readyRequest.getRequester().getId());
    }

    private void checkParametersOfRequest(Request request, Long userId, Long eventId) {
        if (Boolean.FALSE.equals(request.getEvent().getInitiator().getId().equals(userId))) {
            throw new ValidationException("The requester does not match the passed value");
        }
        if (Boolean.FALSE.equals(request.getEvent().getId().equals(eventId))) {
            throw new ValidationException("The event does not match the passed value");
        }
    }

    private void checkInitiatorId(Event event, Long userId) {
        if (Boolean.FALSE.equals(event.getInitiator().getId().equals(userId))) {
            throw new ValidationException("The initiator of the event does not match the passed value");
        }
    }

}
