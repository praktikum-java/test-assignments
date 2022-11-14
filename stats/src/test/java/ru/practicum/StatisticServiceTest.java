package ru.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.entity.AdditionalFields;
import ru.practicum.entity.Statistic;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatisticRepository;
import ru.practicum.service.StatisticServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceTest {
    @InjectMocks
    private StatisticServiceImpl statisticService;
    @Mock
    private StatisticRepository statisticRepository;

    // save
    @Test
    public void shouldPutObjectInStorage() {
        LocalDateTime timePoint = LocalDateTime.now().minusMinutes(5);
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/event/367")
                .ip("172.16.255.254")
                .timestamp(timePoint)
                .build();

        AdditionalFields additionalFields = new AdditionalFields(endpointHit.getApp(), endpointHit.getIp());
        Statistic statisticToCheck = Statistic.builder()
                .uri(endpointHit.getUri())
                .createdAt(endpointHit.getTimestamp())
                .additionalFields(additionalFields)
                .build();
        statisticService.save(endpointHit);
        Mockito.verify(statisticRepository, Mockito.times(1))
                .save(statisticToCheck);
    }

    // getStats
    @Test
    public void shouldGetListViewStatsNonUnique() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String[] uris = {"/event/367"};
        Boolean unique = false;

        AdditionalFields additionalFields = new AdditionalFields("ewm-main-service", "172.16.255.254");
        Statistic statistic = Statistic.builder()
                .id(1L)
                .uri("/event/367")
                .createdAt(LocalDateTime.now().minusDays(15))
                .additionalFields(additionalFields)
                .build();
        when(statisticRepository.findByUri("/event/367")).thenReturn(Optional.of(statistic));

        when(statisticRepository.getStatisticWithoutUniqueIp("/event/367", "ewm-main-service", start, end))
                .thenReturn(99L);

        ViewStats viewStats = ViewStats.builder()
                .app("ewm-main-service")
                .uri("/event/367")
                .hits(99L)
                .build();
        List<ViewStats> result = statisticService.getStats(start, end, uris, unique);
        List<ViewStats> statisticToCheck = List.of(viewStats);
        assertEquals(statisticToCheck, result);
    }

    @Test
    public void shouldGetListViewStatsUnique() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String[] uris = {"/event/367"};
        Boolean unique = true;

        AdditionalFields additionalFields = new AdditionalFields("ewm-main-service", "172.16.255.254");
        Statistic statistic = Statistic.builder()
                .id(1L)
                .uri("/event/367")
                .createdAt(LocalDateTime.now().minusDays(15))
                .additionalFields(additionalFields)
                .build();
        when(statisticRepository.findByUri("/event/367")).thenReturn(Optional.of(statistic));

        when(statisticRepository.getStatisticWithUniqueIp("/event/367", "ewm-main-service", start, end))
                .thenReturn(99L);

        ViewStats viewStats = ViewStats.builder()
                .app("ewm-main-service")
                .uri("/event/367")
                .hits(99L)
                .build();
        List<ViewStats> result = statisticService.getStats(start, end, uris, unique);
        List<ViewStats> statisticToCheck = List.of(viewStats);
        assertEquals(statisticToCheck, result);
    }

    @Test
    public void shouldThrowExceptionWhenStatisticNotFound() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String[] uris = {"/event/367"};
        Boolean unique = true;

        when(statisticRepository.findByUri("/event/367")).thenReturn(Optional.empty());

        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> statisticService.getStats(start, end, uris, unique));
        assertEquals("Statistic not found", re.getMessage());
    }

    // getViews
    @Test
    public void shouldReturnViews() {
        String uri = "/event/367";
        when(statisticRepository.countByUri(uri)).thenReturn(Optional.of(5L));
        Long result = statisticService.getViews(uri);
        assertEquals(5L, result);
    }

}
