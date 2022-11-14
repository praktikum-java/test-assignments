package ru.practicum.events.dto.publ;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.users.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO с кратким описанием события")
public class EventShortDto {
    @NotNull
    @Schema(description = "Краткое описание")
    private String annotation;
    @NotNull
    @Schema(description = "Категория", example = "{\"id\": 1, \"name\": \"Концерты\"}")
    private CategoryDto category;
    @Schema(description = "Количество одобренных заявок на участие в данном событии")
    private Long confirmedRequests;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "дата начала события формате yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Schema(description = "")
    private Long id;
    @NotNull
    @Schema(description = "Пользователь (краткая информация)", example = "{\"id\": 3, \"name\": \"Фёдоров Матвей\"}")
    private UserShortDto initiator;
    @NotNull
    @Schema(description = "Нужно ли оплачивать участие")
    private Boolean paid;
    @NotNull
    @Schema(description = "Заголовок")
    private String title;
    @Schema(description = "Количество просмотрев события")
    private Long views;
}
