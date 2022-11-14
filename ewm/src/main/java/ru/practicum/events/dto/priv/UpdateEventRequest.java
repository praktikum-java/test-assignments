package ru.practicum.events.dto.priv;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для изменения информации о событии")
public class UpdateEventRequest {
    @Length(min = 20, max = 2000)
    @Schema(description = "Новая аннотация")
    private String annotation;
    @Positive
    @Schema(description = "Новая категория")
    private Long category;
    @Length(min = 20, max = 7000)
    @Schema(description = "Новое описание")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Новые дата и время на которые намечено событие. Дата и время указываются в формате yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    @Positive
    @Schema(description = "Идентификатор события")
    private Long eventId;
    @Schema(description = "Новое значение флага о платности мероприятия")
    private Boolean paid;
    @Positive
    @Schema(description = "Новый лимит пользователей")
    private Long participantLimit;
    @Length(min = 3, max = 120)
    @Schema(description = "Новый заголовок")
    private String title;
}
