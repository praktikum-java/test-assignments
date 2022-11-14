package ru.practicum.events.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.entity.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.client.StatisticClient;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.dto.admin.AdminUpdateEventRequest;
import ru.practicum.events.dto.publ.EventFullDto;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.specification.EventFindSpecification;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;
import ru.practicum.util.UtilCollectorsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final StatisticClient statisticClient;

    // Поиск событий.
    /* Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия */
    @Override
    @Transactional
    public List<EventFullDto> getAllEvents(// список id пользователей, чьи события нужно найти
                                           List<Long> users,
                                           // список состояний в которых находятся искомые события
                                           List<EventState> states,
                                           // список id категорий в которых будет вестись поиск
                                           List<Long> categories,
                                           // дата и время не раньше которых должно произойти событие
                                           LocalDateTime rangeStart,
                                           // дата и время не позже которых должно произойти событие
                                           LocalDateTime rangeEnd,
                                           // количество событий, которые нужно пропустить для формирования текущего набора
                                           Integer from,    /* (дефолтно = 0) */
                                           // количество событий в наборе
                                           Integer size     /* (дефолтно = 10) */) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> userEntities = userRepository.findAllById(users);
        List<Category> categoryEntities = categoryRepository.findAllById(categories);
        Page<Event> listEvent;
        if (rangeStart == null || rangeEnd == null) {
            listEvent = eventRepository.findAll(EventFindSpecification
                    .specificationForAdminSearchWithoutDate(userEntities, states, categoryEntities), pageable);
        } else {
            listEvent = eventRepository.findAll(EventFindSpecification
                    .specificationForAdminSearchWithDate(userEntities, states,
                            categoryEntities, rangeStart, rangeEnd), pageable);
        }
        return UtilCollectorsDto.getListEventFullDto(listEvent.toList(), categoryMapper, userMapper,
                statisticClient, eventMapper, requestRepository);
    }

    // Редактирование событий.
    /* Редактирование данных любого события администратором. Валидация данных не требуется. */
    @Override
    @Transactional(readOnly = false)
    public EventFullDto changeEvent(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            Category category = findCategoryOrReturnNull(adminUpdateEventRequest.getCategory());
            eventMapper.updateEventFromAdminUpdateEventRequest(adminUpdateEventRequest, event, category);
            Event readyEvent = eventRepository.save(event);
            return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                    statisticClient, eventMapper, requestRepository);
        } else {
            LocalDateTime createdOn = LocalDateTime.now();
            Category category = findCategoryOrReturnNull(adminUpdateEventRequest.getCategory());
            Event event = eventMapper.toEventFromAdminUpdateEventRequest(eventId, adminUpdateEventRequest, category,
                    EventState.PENDING, createdOn);
            Event readyEvent = eventRepository.save(event);
            return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                    statisticClient, eventMapper, requestRepository);
        }
    }

    private Category findCategoryOrReturnNull(Long catId) {
        Category category = null;
        if (catId != null) {
            category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new CustomNotFoundException("Category not found"));
        }
        return category;
    }


    // Публикация события.
    /* Дата начала события должна быть не ранее чем за час от даты публикации.
     * Событие должно быть в состоянии ожидания публикации*/
    @Override
    @Transactional(readOnly = false)
    public EventFullDto publishingEvent(Long eventId) {
        Event eventEntity = eventRepository.findById(eventId).orElseThrow(() -> new CustomNotFoundException("Event not found"));
        if (eventEntity.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("The start of the event cannot be earlier than in an hour");
        }
        if (Boolean.FALSE.equals(eventEntity.getState().equals(EventState.PENDING))) {
            throw new ValidationException("The publication of the event can only be from the PENDING status");
        }
        eventEntity.setState(EventState.PUBLISHED);
        Event readyEvent = eventRepository.save(eventEntity);
        return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                statisticClient, eventMapper, requestRepository);
    }

    // Отклонение события.
    // Обратите внимание: событие не должно быть опубликовано.
    @Override
    @Transactional(readOnly = false)
    public EventFullDto rejectEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new CustomNotFoundException("Event not found"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("The event has already been published");
        }
        event.setState(EventState.CANCELED);
        Event readyEvent = eventRepository.save(event);
        return UtilCollectorsDto.getEventFullDto(readyEvent, categoryMapper, userMapper,
                statisticClient, eventMapper, requestRepository);
    }
}
