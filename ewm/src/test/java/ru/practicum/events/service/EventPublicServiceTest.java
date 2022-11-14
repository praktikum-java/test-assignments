package ru.practicum.events.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.categories.dto.CategoryMapperImpl;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.client.StatisticClient;
import ru.practicum.events.dto.EventMapperImpl;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.dto.publ.EventShortDto;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.publ.EventPublicServiceImpl;
import ru.practicum.events.specification.EventFindSpecification;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.dto.UserMapperImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventPublicServiceTest {
    @InjectMocks
    private EventPublicServiceImpl eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private StatisticClient statisticClient;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private CategoryMapperImpl categoryMapper;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private EventFindSpecification eventFindSpecification;
    @Mock
    private HttpServletRequest request;

    // getEvents
    @Test
    public void shouldGetAllEventsByUserId() {

        String text = "Sport";          // текст для поиска в содержимом аннотации и подробном описании события
        long[] categories = {1L};     // список идентификаторов категорий в которых будет вестись поиск
        Boolean paid = false;         // поиск только платных/бесплатных событий
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(10);    // дата и время не раньше которых должно произойти событие
        LocalDateTime rangeEnd = LocalDateTime.now();      // дата и время не позже которых должно произойти событие
        Boolean onlyAvailable = true;  // только события у которых не исчерпан лимит запросов на участие
        String sort = "VIEWS";          /* Вариант сортировки: по дате события или по количеству просмотров
                                                                                Available values : EVENT_DATE, VIEWS */
        Integer from = 1;       // количество событий, которые нужно пропустить для формирования текущего набора
        Integer size = 20;
        Category category1 = Category.builder().id(1L).name("Category1").build();
        List<Category> listCategories = List.of(category1);
        when(categoryRepository.findAllByIdIn(List.of(1L))).thenReturn(listCategories);

        Event event1 = Event.builder().id(1L).title("Sport").build();
        Page<Event> page = new PageImpl<>(List.of(event1));
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        List<EventShortDto> result = eventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
        EventShortDto eventDto1 = EventShortDto.builder().id(1L).title("Sport").confirmedRequests(0L).views(0L).build();

        assertEquals(List.of(eventDto1), result);
    }

    // getEventById
    @Test
    public void shouldGetEventById() {
        Event event1 = Event.builder().id(1L).title("Sport").state(EventState.PUBLISHED).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        EventFullDto result = eventService.getEventById(1L, request);
        EventFullDto eventToCheck = EventFullDto.builder().id(1L).title("Sport").confirmedRequests(0L).views(0L)
                .state(EventState.PUBLISHED).build();
        assertEquals(eventToCheck, result);
    }
}
