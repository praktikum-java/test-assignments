package ru.practicum.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.events.controller.admin.EventAdminController;
import ru.practicum.events.dto.admin.AdminUpdateEventRequest;
import ru.practicum.events.service.admin.EventAdminService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventAdminController.class)
public class EventAdminControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventAdminService eventAdminService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String rangeStart = LocalDateTime.now().minusDays(10).format(formatter);
    String rangeEnd = LocalDateTime.now().format(formatter);
    AdminUpdateEventRequest adminUpdateEventRequest = AdminUpdateEventRequest.builder().description("Party").build();

    // getAllEvents
    @Test
    public void shouldGetParametersAndCallServiceWithBody() throws Exception {
        mockMvc.perform(get("/admin/events")
                        .param("rangeStart", rangeStart)
                        .param("rangeEnd", rangeEnd)
                        .param("from", "1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(eventAdminService, Mockito.times(1))
                .getAllEvents(eq(null), eq(null), eq(null), any(LocalDateTime.class),
                        any(LocalDateTime.class), eq(1), eq(20));
    }

    // changeEvent
    @Test
    public void shouldUpdateEventAndCallServiceWithBody() throws Exception {
        mockMvc.perform(put("/admin/events/{eventId}", 1)
                        .content(objectMapper.writeValueAsString(adminUpdateEventRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(eventAdminService, Mockito.times(1))
                .changeEvent(1L, adminUpdateEventRequest);
    }

    // publishingEvent
    @Test
    public void shouldGetPublishRequestAndCallServiceWithBody() throws Exception {
        mockMvc.perform(patch("/admin/events//{eventId}/publish", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(eventAdminService, Mockito.times(1))
                .publishingEvent(1L);
    }

    // rejectEvent
    @Test
    public void shouldGetRejectRequestAndCallServiceWithBody() throws Exception {
        mockMvc.perform(patch("/admin/events//{eventId}/reject", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(eventAdminService, Mockito.times(1))
                .rejectEvent(1L);
    }

}
