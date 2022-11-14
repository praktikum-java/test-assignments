package ru.practicum.events.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.events.controller.publ.EventPublicController;
import ru.practicum.events.service.publ.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventPublicController.class)

public class EventPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventPublicService eventPublicService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String rangeStart = LocalDateTime.now().minusDays(10).format(formatter);
    String rangeEnd = LocalDateTime.now().format(formatter);

    // getEvents
    @Test
    public void shouldGetParametersAndCallServiceWithBody() throws Exception {
        mockMvc.perform(get("/events")
                        .param("text", "art")
                        .param("paid", "false")
                        .param("rangeStart", rangeStart)
                        .param("rangeEnd", rangeEnd)
                        .param("onlyAvailable", "true")
                        .param("sort", "VIEWS")
                        .param("from", "1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(eventPublicService, Mockito.times(1))
                .getEvents(eq("art"), eq(null), eq(false), any(LocalDateTime.class), any(LocalDateTime.class),
                        eq(true), eq("VIEWS"), eq(1), eq(20), any(HttpServletRequest.class));
    }


    // getEventById
    @Test
    public void shouldGetRequestAndCallServiceWithBody() throws Exception {
        mockMvc.perform(get("/events/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(eventPublicService, Mockito.times(1))
                .getEventById(eq(1L), any(HttpServletRequest.class));
    }
}
