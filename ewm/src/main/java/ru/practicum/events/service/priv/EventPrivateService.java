package ru.practicum.events.service.priv;

import ru.practicum.events.dto.priv.NewEventDto;
import ru.practicum.events.dto.priv.UpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {
    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto changeEventByUser(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto cancellationEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto acceptParticipationRequest(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectParticipationRequest(Long userId, Long eventId, Long reqId);

}
