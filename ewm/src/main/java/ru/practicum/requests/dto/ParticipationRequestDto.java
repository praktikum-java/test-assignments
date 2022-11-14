package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Заявка на участие в событии")
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время создания заявки")
    private LocalDateTime created;
    @Schema(description = "Идентификатор события")
    private Long event;
    @Schema(description = "Идентификатор заявки")
    private Long id;
    @Schema(description = "Идентификатор пользователя, отправившего заявку")
    private Long requester;
    @Schema(description = "Статус заявки.  PENDING, CONFIRMED, REJECTED")
    private RequestStatus status;
}
