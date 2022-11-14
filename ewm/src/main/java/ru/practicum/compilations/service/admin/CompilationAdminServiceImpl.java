package ru.practicum.compilations.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.entity.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.util.UtilCollectorsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationMapper compilationMapper;
    private final StatisticClient statisticClient;
    private final EventMapper eventMapper;

    // Добавление новой подборки.
    @Override
    @Transactional(readOnly = false)
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventEntities = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, eventEntities);
        Compilation readyCompilation = compilationRepository.save(compilation);
        return UtilCollectorsDto.getCompilationDto(readyCompilation, statisticClient,
                eventMapper, compilationMapper, requestRepository);
    }

    // Удаление подборки.
    @Override
    @Transactional(readOnly = false)
    public void removeCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    // Удалить событие из подборки. Возвращает только статус ответа или ошибку.
    @Override
    @Transactional(readOnly = false)
    public void removeEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CustomNotFoundException("Compilation not found"));
        List<Event> events = new ArrayList<>(compilation.getEvents());
        Event event = events.stream().filter(e -> e.getId().equals(eventId)).findFirst()
                .orElseThrow(() -> new CustomNotFoundException("Event not found in the compilation list"));
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        System.out.println("Сейчас сборка выглядит так: " + compilation);

    }

    // Добавить событие в подборку. Возвращает только статус ответа или ошибку.
    @Override
    @Transactional(readOnly = false)
    public void addEventInCompilation(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CustomNotFoundException("Compilation not found"));
        List<Event> eventsOfCompilation = new ArrayList<>(compilation.getEvents());
        Optional<Event> optionalEventFromList = eventsOfCompilation
                .stream().filter(e -> e.getId().equals(eventId)).findFirst();
        if (optionalEventFromList.isPresent()) {
            throw new ValidationException("Event already exists in compilation");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomNotFoundException("Event not found"));
        eventsOfCompilation.add(event);
        compilation.setEvents(eventsOfCompilation);
        compilationRepository.save(compilation);
        System.out.println("Сейчас сборка выглядит так: " + compilation);
    }

    // Открепить подборку на главной странице. Возвращает только статус ответа или ошибку.
    @Override
    @Transactional(readOnly = false)
    public void unpinCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CustomNotFoundException("Compilation not found"));
        if (compilation.getPinned().equals(false)) {
            throw new ValidationException("Pinned already false");
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    // Закрепить подборку на главной странице. Возвращает только статус ответа или ошибку.
    @Override
    @Transactional(readOnly = false)
    public void pinCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CustomNotFoundException("Compilation not found"));
        if (compilation.getPinned().equals(true)) {
            throw new ValidationException("Pinned already false");
        }
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }
}
