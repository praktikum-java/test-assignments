package ru.practicum.events.dto.priv;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.events.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания новой категории")
public class NewEventDto {
    @NotNull
    @Length(min = 20, max = 2000)
    @Schema(description = "Краткое описание события")
    private String annotation;
    @NotNull
    @Positive
    @Schema(description = "id категории к которой относится событие")
    private Long category;
    @NotNull
    @Length(min = 20, max = 7000)
    @Schema(description = "Полное описание события")
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время на которые намечено событие. Дата и время указываются в формате yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    @Schema(description = "Широта и долгота места проведения события")
    private Location location;
    @Schema(description = "Нужно ли оплачивать участие в событии. Значение по умолчанию = false")
    private Boolean paid = false;
    @Positive
    @Schema(description = "Ограничение на количество участников. Значение 0 - означает отсутствие ограничения")
    private Long participantLimit = 0L;
    @Schema(description = "Нужна ли пре-модерация заявок на участие. Если true, то все заявки " +
            "будут ожидать подтверждения инициатором события. Если false - то будут подтверждаться автоматически")
    private Boolean requestModeration = true; /* . */
    @NotNull
    @Length(min = 3, max = 120)
    @Schema(description = "Заголовок события")
    private String title;
}
