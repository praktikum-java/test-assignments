package ru.practicum.compilations.service.publ;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.client.StatisticClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.util.UtilCollectorsDto;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.UtilCollectorsDto.getCompilationDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatisticClient statisticClient;

    @Override
    @Transactional
    //Получение подборок событий.
    // Искать только закрепленные/не закрепленные подборки
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        return compilations.stream()
                .map(e -> UtilCollectorsDto.getCompilationDto(e, statisticClient, eventMapper,
                        compilationMapper, requestRepository))
                .collect(Collectors.toList());


    }

    @Override
    @Transactional
    // Получение подборки событий по id.
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CustomNotFoundException("Compilation not found"));
        System.out.println("\n Из БД вытащили объект Compilation: " + compilation);
        return getCompilationDto(compilation, statisticClient, eventMapper, compilationMapper, requestRepository);
    }
}
