package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.client.StatisticClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.events.entity.Event;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.dto.UserShortDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UtilCollectorsDto {

    public static List<EventShortDto> getListEventShortDto(List<Event> events,
                                                           StatisticClient statisticClient,
                                                           EventMapper eventMapper, RequestRepository requestRepository) {
        return events.stream()
                .map(e -> UtilCollectorsDto.getEventShortDto(e, eventMapper, statisticClient, requestRepository))
                .collect(Collectors.toList());
    }

    public static EventShortDto getEventShortDto(Event event, EventMapper eventMapper,
                                                 StatisticClient statisticClient, RequestRepository requestRepository) {
        Long confirmedRequests = requestRepository
                .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).orElse(0L);
        Long views = statisticClient.getViewsByUri(event.getId());
        return eventMapper.toEventShortDto(event, confirmedRequests, views);
    }

    public static List<EventFullDto> getListEventFullDto(List<Event> events, CategoryMapper categoryMapper,
                                                         UserMapper userMapper, StatisticClient statisticClient,
                                                         EventMapper eventMapper, RequestRepository requestRepository) {
        return events.stream()
                .map(event -> UtilCollectorsDto.getEventFullDto(event, categoryMapper, userMapper,
                        statisticClient, eventMapper, requestRepository))
                .collect(Collectors.toList());
    }

    public static EventFullDto getEventFullDto(Event event, CategoryMapper categoryMapper, UserMapper userMapper,
                                               StatisticClient statisticClient, EventMapper eventMapper,
                                               RequestRepository requestRepository) {
        CategoryDto categoryDto = categoryMapper.toCategoryDto(event.getCategory());
        Long confirmedRequests = requestRepository
                .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).orElse(0L);
        UserShortDto initiator = userMapper.toUserShortDto(event.getInitiator());
        Long views = 0L;
        if (statisticClient != null) {
            views = statisticClient.getViewsByUri(event.getId());
        }
        return eventMapper.toEventFullDto(event, categoryDto, confirmedRequests,
                initiator, views);
    }

    public static CompilationDto getCompilationDto(Compilation compilation,
                                                   StatisticClient statisticClient,
                                                   EventMapper eventMapper,
                                                   CompilationMapper compilationMapper,
                                                   RequestRepository requestRepository) {
        List<EventShortDto> eventsDto = getListEventShortDto(compilation.getEvents(),
                statisticClient, eventMapper, requestRepository);
        return compilationMapper.toCompilationDto(compilation, eventsDto);
    }

}
