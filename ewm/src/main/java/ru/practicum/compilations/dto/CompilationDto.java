package ru.practicum.compilations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.UniqueElements;
import ru.practicum.events.dto.publ.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
@Schema(description = "Подборка событий")
public class CompilationDto {
    @UniqueElements
    @Schema(description = "Подборка событий")
    List<EventShortDto> events;
    @NotNull
    @Schema(description = "Идентификатор")
    Long id;
    @NotNull
    @Schema(description = "Закреплена ли подборка на главной странице сайта")
    Boolean pinned;
    @NotBlank
    @Schema(description = "Заголовок подборки")
    String title;
}
