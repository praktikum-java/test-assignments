package ru.practicum.compilations.service.admin;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;

public interface CompilationAdminService {
    CompilationDto createNewCompilation(NewCompilationDto newCompilationDto);

    void removeCompilation(Long compId);

    void removeEventFromCompilation(Long compId, Long eventId);

    void addEventInCompilation(Long compId, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);
}
