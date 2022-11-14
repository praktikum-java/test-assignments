package ru.practicum.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ViewStats {
    @Schema(description = "Название сервиса", example = "ewm-main-service")
    String app;
    @Schema(description = "URI сервиса", example = "/event/367")
    String uri;
    @Schema(description = "Количество просмотров")
    Long hits;
}
