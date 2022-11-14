package ru.practicum.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewStat {
    private String app;         // Название сервиса
    private String uri;         // URI сервиса
    private Long hits;       // Количество просмотров
}
