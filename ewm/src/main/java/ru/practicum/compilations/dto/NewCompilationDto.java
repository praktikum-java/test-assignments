package ru.practicum.compilations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания новой подборки событий")
public class NewCompilationDto {
    @UniqueElements
    @Schema(description = "Список идентификаторов событий входящих в подборку")
    private List<Long> events;
    @Schema(description = "Закреплена ли подборка на главной странице сайта")
    private Boolean pinned = false;
    @NotBlank
    @Schema(description = "Заголовок подборки")
    private String title;
}
