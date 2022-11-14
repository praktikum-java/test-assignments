package ru.practicum.compilations.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.events.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "events", source = "events")
    Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events);

    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events);
}
