package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    @Schema(description = "Идентификатор записи")
    private Long id;
    @Schema(description = "Идентификатор сервиса для которого записывается информация", example = "ewm-main-service")
    private String app;
    @Schema(description = "URI для которого был осуществлен запрос", example = "/event/367")
    private String uri;
    @Schema(description = "IP-адрес пользователя, осуществившего запрос", example = "172.16.255.254")
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время, когда был совершен/ запрос к эндпоинту (в формате yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime timestamp;
}
