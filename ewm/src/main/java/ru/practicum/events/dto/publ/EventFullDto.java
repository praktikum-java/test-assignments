package ru.practicum.events.dto.publ;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.model.EventState;
import ru.practicum.events.model.Location;
import ru.practicum.users.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO с полным описанием события")
public class EventFullDto {
    @NotNull
    @Schema(description = "Краткое описание")
    private String annotation;
    @NotNull
    @Schema(description = "Категория", example = "{\"id\": 1, \"name\": \"Концерты\"}")
    private CategoryDto category;
    @Schema(description = "Количество одобренных заявок на участие в данном событии")
    private Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время создания события (в формате yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime createdOn;
    @Schema(description = "Полное описание события")
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время на которые намечено событие (в формате yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime eventDate;
    private Long id;
    @NotNull
    @Schema(description = "Пользователь (краткая информация)", example = "{\"id\": 3, \"name\": \"Фёдоров Матвей\"}")
    private UserShortDto initiator;
    @NotNull
    @Schema(description = "Широта и долгота места проведения события", example = "{\"lat\": 55.754167, \"lon\": 37.62}")
    private Location location;
    @NotNull
    @Schema(description = "Нужно ли оплачивать участие")
    private Boolean paid;
    @Schema(description = "Ограничение на количество участников. Значение 0 - означает отсутствие ограничения")
    private Long participantLimit = 0L;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время публикации события (в формате yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime publishedOn;
    @Schema(description = "Нужна ли пре-модерация заявок на участие. Дефолтно: true")
    private Boolean requestModeration = true;
    @Schema(description = "Список состояний жизненного цикла события [PENDING, PUBLISHED, CANCELED]")
    private EventState state;
    @NotNull
    @Schema(description = "Заголовок")
    private String title;
    @Schema(description = "Количество просмотрев события")
    private Long views;
}
