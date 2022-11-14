package ru.practicum.events.service.admin;

import ru.practicum.events.dto.admin.AdminUpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {

    List<EventFullDto> getAllEvents(List<Long> users,
                                    List<EventState> states,
                                    List<Long> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Integer from,
                                    Integer size);

    EventFullDto changeEvent(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto publishingEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);
}
