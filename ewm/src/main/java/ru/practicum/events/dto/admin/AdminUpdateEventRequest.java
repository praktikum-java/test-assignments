package ru.practicum.events.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация для редактирования события администратором. Все поля необязательные. Значение полей не валидируются")
public class AdminUpdateEventRequest {
    @Schema(description = "Краткое описание события")
    private String annotation;
    @Schema(description = "id категории к которой относится событие")
    private Long category;
    @Schema(description = "Полное описание события")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время на которые намечено событие (в формате yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime eventDate;
    @Schema(description = "Широта и долгота места проведения события")
    private Location location;
    @Schema(description = "Нужно ли оплачивать участие в событии")
    private Boolean paid;
    @Schema(description = "Ограничение на количество участников. Значение 0 - означает отсутствие ограничения")
    private Long participantLimit;
    @Schema(description = "Нужна ли пре-модерация заявок на участие")
    private Boolean requestModeration = false;
    @Schema(description = "Заголовок события")
    private String title;
}
