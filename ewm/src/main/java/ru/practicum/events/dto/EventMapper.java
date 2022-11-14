package ru.practicum.events.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.entity.Category;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.events.dto.admin.AdminUpdateEventRequest;
import ru.practicum.events.dto.priv.NewEventDto;
import ru.practicum.events.dto.priv.UpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "availableForRequest", source = "availableForRequest")
    Event toEventFromNewEventDto(NewEventDto newEventDto, Category category, User initiator, EventState state,
                                 LocalDateTime createdOn, LocalDateTime publishedOn, List<Compilation> compilations,
                                 Boolean availableForRequest);

    @Mapping(target = "id", source = "event.id")
    EventFullDto toEventFullDto(Event event, CategoryDto category, Long confirmedRequests,
                                UserShortDto initiator, Long views);

    EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "category", source = "category")
    Event toEventFromAdminUpdateEventRequest(Long id, AdminUpdateEventRequest adminUpdateEventRequest, Category category,
                                             EventState state, LocalDateTime createdOn);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    void updateEventFromAdminUpdateEventRequest(AdminUpdateEventRequest adminUpdateEventRequest,
                                                @MappingTarget Event event, Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    void updateEventFromUpdateEventRequest(UpdateEventRequest updateEventRequest,
                                           @MappingTarget Event event, Category category);
}
