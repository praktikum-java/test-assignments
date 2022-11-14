package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    // Получение информации о заявках текущего пользователя на участие в чужих событиях.
    @Override
    @Transactional
    // Получение информации о заявках текущего пользователя на участие в чужих событиях.
    public List<ParticipationRequestDto> getParticipationRequest(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(e -> requestMapper.toRequestDto(e, e.getEvent().getId(), e.getRequester().getId()))
                .collect(Collectors.toList());
    }

    // Добавление запроса от текущего пользователя на участие в событии.
    /* Нельзя добавить повторный запрос
     * Инициатор события не может добавить запрос на участие в своём событии
     * Нельзя участвовать в неопубликованном событии
     * Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
     * Если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти
     * в состояние подтвержденного*/
    @Override
    @Transactional(readOnly = false)
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidationException("Request already exists");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new CustomNotFoundException("Event not found"));
        if (event.getAvailableForRequest().equals(false)) {
            throw new ValidationException("The request limit has been reached for this event");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("The initiator of the event cannot make a request for it");
        }
        if (Boolean.FALSE.equals(event.getState().equals(EventState.PUBLISHED))) {
            throw new ValidationException("Event not published");
        }
        User requester = userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("User not found"));
        LocalDateTime currentTime = LocalDateTime.now();
        Request request;
        if (event.getRequestModeration().equals(true)) {
            request = Request.builder()
                    .event(event)
                    .requester(requester)
                    .status(RequestStatus.PENDING)
                    .created(currentTime)
                    .build();
        } else {
            request = Request.builder()
                    .event(event)
                    .requester(requester)
                    .status(RequestStatus.CONFIRMED)
                    .created(currentTime)
                    .build();
        }
        Request readyRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(readyRequest, readyRequest.getEvent().getId(), readyRequest.getRequester().getId());
    }

    // Отмена своего запроса на участие в событии.
    @Override
    @Transactional(readOnly = false)
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomNotFoundException("Request not found"));
        if (Boolean.FALSE.equals(request.getRequester().getId().equals(userId))) {
            throw new ValidationException("The requester does not match the received id");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request readyRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(readyRequest, readyRequest.getEvent().getId(), readyRequest.getRequester().getId());
    }
}
