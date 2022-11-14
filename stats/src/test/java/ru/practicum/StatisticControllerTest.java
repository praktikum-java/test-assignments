package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.controller.StatisticController;
import ru.practicum.model.EndpointHit;
import ru.practicum.service.StatisticService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticController.class)
public class StatisticControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatisticService statisticService;

    // save
    @Test
    public void shouldCallServiceSave() throws Exception {
        LocalDateTime timePoint = LocalDateTime.now().minusMinutes(5);
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/event/367")
                .ip("172.16.255.254")
                .timestamp(timePoint)
                .build();
        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHit))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(statisticService, Mockito.times(1))
                .save(any(EndpointHit.class));
    }

    // getStatistic
    @Test
    public void shouldCallServiceGetStats() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String rangeStart = LocalDateTime.now().minusDays(10).format(formatter);
        String rangeEnd = LocalDateTime.now().format(formatter);
        String[] uris = {"/event/367"};
        Boolean unique = false;
        mockMvc.perform(get("/stats")
                        .param("start", rangeStart)
                        .param("end", rangeEnd)
                        .param("uris", objectMapper.writeValueAsString(uris))
                        .param("unique", objectMapper.writeValueAsString(unique))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(statisticService, Mockito.times(1))
                .getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(), any(Boolean.class));
    }

    //     getViews
    @Test
    public void shouldCallServiceGetViews() throws Exception {
        String uri = "/events/" + 1;
        mockMvc.perform(get(uri)
//                        .content(uri)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(statisticService, Mockito.times(1))
                .getViews(uri);
    }
}
