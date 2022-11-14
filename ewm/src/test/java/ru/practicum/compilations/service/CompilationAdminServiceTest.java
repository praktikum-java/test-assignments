package ru.practicum.compilations.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.client.StatisticClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapperImpl;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.service.admin.CompilationAdminServiceImpl;
import ru.practicum.events.dto.EventMapperImpl;
import ru.practicum.events.entity.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.CustomNotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompilationAdminServiceTest {
    @InjectMocks
    private CompilationAdminServiceImpl compilationService;
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private CompilationMapperImpl compilationMapper;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private StatisticClient statisticClient;

    NewCompilationDto newCompilationDto = NewCompilationDto.builder()
            .events(Collections.emptyList()).pinned(true).title("Weekend").build();
    CompilationDto compilationDto1 = CompilationDto.builder()
            .id(1L).events(Collections.emptyList()).pinned(true).title("Weekend").build();
    Compilation preCompilation1 = Compilation.builder().title("Weekend").pinned(true)
            .events(Collections.emptyList()).build();
    Compilation savedCompilation1 = Compilation.builder().id(1L).title("Weekend").pinned(true)
            .events(Collections.emptyList()).build();
    Event event1 = Event.builder().id(1L).build();
    Event event2 = Event.builder().id(2L).build();

    //     createNewCompilation
    @Test
    public void shouldCreateCompilationAndReturnDto() {
        when(compilationRepository.save(preCompilation1)).thenReturn(savedCompilation1);
        CompilationDto result = compilationService.createNewCompilation(newCompilationDto);
        CompilationDto compilationToCheck = compilationDto1;
        assertEquals(compilationToCheck, result);
    }

    // removeCompilation
    @Test
    public void shouldRemoveCompilationAndCallRepository() {
        compilationService.removeCompilation(1L);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .deleteById(1L);
    }

    // removeEventFromCompilation
    @Test
    public void shouldRemoveEventFromCompilation() {
        Compilation compilation0 = Compilation.builder()
                .events(List.of(event1, event2)).pinned(true).title("Weekend").build();
        Compilation compilationWithoutEvent = Compilation.builder()
                .events(List.of(event1)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        when(compilationRepository.save(compilation0)).thenReturn(compilationWithoutEvent);
        compilationService.removeEventFromCompilation(1L, 1L);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(any(Compilation.class));
    }

    @Test
    public void shouldNotFindCompilationAndThrowException() {
        when(compilationRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> compilationService.removeEventFromCompilation(99L, 1L));
        assertEquals("Compilation not found", re.getMessage());
    }

    @Test
    public void shouldNotFindEventInCompilationListAndThrowException() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1, event2)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> compilationService.removeEventFromCompilation(1L, 99L));
        assertEquals("Event not found in the compilation list", re.getMessage());
    }

    // addEventInCompilation
    @Test
    public void shouldAddEventToCompilation() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1)).pinned(true).title("Weekend").build();
        Compilation compilationWithNewEvent = Compilation.builder()
                .events(List.of(event1, event2)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event2));
        when(compilationRepository.save(compilation0)).thenReturn(compilationWithNewEvent);
        compilationService.addEventInCompilation(1L, 2L);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(any(Compilation.class));
    }

    @Test
    public void shouldThrowExceptionWhenCompilationNotFound() {
        when(compilationRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> compilationService.addEventInCompilation(99L, 1L));
        assertEquals("Compilation not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenEventAlreadyExists() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1, event2)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> compilationService.addEventInCompilation(1L, 2L));
        assertEquals("Event already exists in compilation", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenEventNotFound() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> compilationService.addEventInCompilation(1L, 99L));
        assertEquals("Event not found", re.getMessage());
    }

    // unpinCompilation
    @Test
    public void shouldUnpinCompilationAndCallRepository() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1, event2)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        compilationService.unpinCompilation(1L);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(any(Compilation.class));
    }

    @Test
    public void shouldThrowExceptionWhenWeTryUnpinCompilationAndCompilationNotFound() {
        when(compilationRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> compilationService.unpinCompilation(99L));
        assertEquals("Compilation not found", re.getMessage());
        Mockito.verify(compilationRepository, Mockito.times(0))
                .save(any(Compilation.class));
    }

    @Test
    public void shouldThrowExceptionWhenWeTryUnpinCompilationWhichAlreadyUnpin() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1, event2)).pinned(false).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> compilationService.unpinCompilation(1L));
        assertEquals("Pinned already false", re.getMessage());
        Mockito.verify(compilationRepository, Mockito.times(0))
                .save(any(Compilation.class));
    }

    // pinCompilation
    @Test
    public void shouldPinCompilationAndCallRepository() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1, event2)).pinned(false).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        compilationService.pinCompilation(1L);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(any(Compilation.class));
    }

    @Test
    public void shouldThrowExceptionWhenWeTryPinCompilationAndCompilationNotFound() {
        when(compilationRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(CustomNotFoundException.class,
                () -> compilationService.pinCompilation(99L));
        assertEquals("Compilation not found", re.getMessage());
        Mockito.verify(compilationRepository, Mockito.times(0))
                .save(any(Compilation.class));
    }

    @Test
    public void shouldThrowExceptionWhenWeTryPinCompilationWhichAlreadyUnpin() {
        Compilation compilation0 = Compilation.builder().id(1L)
                .events(List.of(event1, event2)).pinned(true).title("Weekend").build();
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation0));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> compilationService.pinCompilation(1L));
        assertEquals("Pinned already false", re.getMessage());
        Mockito.verify(compilationRepository, Mockito.times(0))
                .save(any(Compilation.class));
    }

}
