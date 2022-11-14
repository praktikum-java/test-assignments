package ru.practicum.events.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapperImpl;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.client.StatisticClient;
import ru.practicum.events.dto.EventMapperImpl;
import ru.practicum.events.dto.admin.AdminUpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.admin.EventAdminServiceImpl;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.dto.UserMapperImpl;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.events.model.EventState.CANCELED;
import static ru.practicum.events.model.EventState.PENDING;
import static ru.practicum.events.model.EventState.PUBLISHED;

@ExtendWith(MockitoExtension.class)
public class EventAdminServiceTest {
    @InjectMocks
    private EventAdminServiceImpl eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private CategoryMapperImpl categoryMapper;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private StatisticClient statisticClient;

    LocalDateTime rangeStart = LocalDateTime.now().minusDays(10);
    LocalDateTime rangeEnd = LocalDateTime.now();
    Integer from = 0;
    Integer size = 10;
    Pageable pageable = PageRequest.of(from / size, size);
    Event event1 = Event.builder().id(1L).build();
    Event event2 = Event.builder().id(2L).build();
    EventFullDto eventDto1 = EventFullDto.builder().id(1L).confirmedRequests(0L).views(0L).build();
    EventFullDto eventDto2 = EventFullDto.builder().id(2L).confirmedRequests(0L).views(0L).build();
    Page<Event> page = new PageImpl<>(List.of(event1, event2));
    User user1 = User.builder().id(1L).build();
    Category category1 = Category.builder().id(1L).build();


    // getAllEvents
    @Test
    public void shouldGetAllEvents() {
        List<Long> users = List.of(1L);
        List<EventState> states = List.of(PUBLISHED);
        List<Long> categories = List.of(1L);
        when(userRepository.findAllById(users)).thenReturn(List.of(user1));
        when(categoryRepository.findAllById(categories)).thenReturn(List.of(category1));

        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        List<EventFullDto> result = eventService.getAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
        assertEquals(List.of(eventDto1, eventDto2), result);
    }

    @Test
    public void shouldGetAllEventsWithoutParameters() {
        when(userRepository.findAllById(null))
                .thenReturn(List.of(user1));
        when(categoryRepository.findAllById(null))
                .thenReturn(List.of(category1));
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        List<EventFullDto> result = eventService.getAllEvents(null, null, null,
                null, null, from, size);
        assertEquals(List.of(eventDto1, eventDto2), result);
    }

    // changeEvent
    @Test
    public void shouldFindAndChangeEvent() {
        Category category0 = Category.builder().id(1L).name("Fun").build();
        Event event0 = Event.builder().id(1L).title("Dance").category(category0).build();
        AdminUpdateEventRequest adminUpdateEventRequest = AdminUpdateEventRequest.builder()
                .title("Party").build();
        Event eventAfterSave = Event.builder().id(1L).title("Party").category(category0).build();
        CategoryDto categoryDto0 = CategoryDto.builder().id(1L).name("Fun").build();
        EventFullDto eventFullDto0 = EventFullDto.builder().id(1L).title("Party").category(categoryDto0)
                .confirmedRequests(0L).views(0L).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event0));
        when(eventRepository.save(any())).thenReturn(eventAfterSave);
        EventFullDto result = eventService.changeEvent(1L, adminUpdateEventRequest);
        assertEquals(eventFullDto0, result);
    }

    @Test
    public void shouldNotFindAndCreateEvent() {
        AdminUpdateEventRequest adminUpdateEventRequest = AdminUpdateEventRequest.builder()
                .title("Party").build();
        Event eventAfterSave = Event.builder().id(1L).title("Party").build();
        EventFullDto eventFullDto0 = EventFullDto.builder().id(1L).title("Party")
                .confirmedRequests(0L).views(0L).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        when(eventRepository.save(any())).thenReturn(eventAfterSave);
        EventFullDto result = eventService.changeEvent(1L, adminUpdateEventRequest);
        assertEquals(eventFullDto0, result);
    }

    @Test
    public void shouldThrowExceptionWhenCategoryNotFound() {
        AdminUpdateEventRequest adminUpdateEventRequest = AdminUpdateEventRequest.builder()
                .title("Party").category(1L).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> eventService.changeEvent(1L, adminUpdateEventRequest));
        assertEquals("Category not found", re.getMessage());
    }

    // publishingEvent
    @Test
    public void shouldSetPublishedStatus() {
        LocalDateTime eventDate = LocalDateTime.now().plusDays(2);
        Event event0 = Event.builder().id(1L).title("Dance").state(PENDING)
                .eventDate(eventDate).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event0));
        Event eventAfterSave = Event.builder().id(1L).title("Dance").state(PUBLISHED)
                .eventDate(eventDate).build();
        when(eventRepository.save(any())).thenReturn(eventAfterSave);

        EventFullDto result = eventService.publishingEvent(1L);
        EventFullDto eventToCheck = EventFullDto.builder().id(1L).title("Dance").state(PUBLISHED)
                .eventDate(eventDate).confirmedRequests(0L).views(0L).build();
        assertEquals(eventToCheck, result);
    }

    @Test
    public void shouldThrowExceptionWhenEventDateEarlierThanTwoHours() {
        LocalDateTime earlierEventDate = LocalDateTime.now().plusMinutes(30);
        Event event0 = Event.builder().id(1L).title("Dance").state(PENDING)
                .eventDate(earlierEventDate).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event0));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> eventService.publishingEvent(1L));
        assertEquals("The start of the event cannot be earlier than in an hour", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> eventService.publishingEvent(1L));
        assertEquals("Event not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenStatusIsNotPending() {
        LocalDateTime earlierEventDate = LocalDateTime.now().plusDays(2);
        Event event0 = Event.builder().id(1L).title("Dance").state(PUBLISHED)
                .eventDate(earlierEventDate).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event0));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> eventService.publishingEvent(1L));
        assertEquals("The publication of the event can only be from the PENDING status", re.getMessage());
    }

    // rejectEvent
    @Test
    public void shouldSetStatusCancelled() {
        Event event0 = Event.builder().id(1L).title("Dance").state(PENDING).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event0));
        Event eventAfterSave = Event.builder().id(1L).title("Dance").state(CANCELED).build();
        when(eventRepository.save(any())).thenReturn(eventAfterSave);
        EventFullDto result = eventService.rejectEvent(1L);
        EventFullDto eventToCheck = EventFullDto.builder().id(1L).title("Dance").state(CANCELED)
                .confirmedRequests(0L).views(0L).build();
        assertEquals(eventToCheck, result);
    }

    @Test
    public void shouldThrowExceptionWhenEventNotFoundFromStorage() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> eventService.rejectEvent(1L));
        assertEquals("Event not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenEventAlreadyFound() {
        Event event0 = Event.builder().id(1L).title("Dance").state(PUBLISHED).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event0));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> eventService.rejectEvent(1L));
        assertEquals("The event has already been published", re.getMessage());
    }
}
