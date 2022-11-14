package ru.practicum.compilations.service.publ;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilations.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(@PathVariable Long compId);
}
