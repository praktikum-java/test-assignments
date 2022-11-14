package ru.practicum.requests.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.requests.entity.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "requester", source = "requester")
    ParticipationRequestDto toRequestDto(Request request, Long event, Long requester);
}
