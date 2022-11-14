package ru.practicum.requests.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.requests.service.RequestService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ParticipationRequestController.class)
public class ParticipationRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;

    // getParticipationRequest
    @Test
    public void shouldCallGetParticipationRequestAndCallServiceWithBody() throws Exception {
        mockMvc.perform(get("/users/{userId}/requests", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(requestService, Mockito.times(1))
                .getParticipationRequest(1L);
    }

    // createParticipationRequest
    @Test
    public void shouldCallCreateParticipationRequestAndCallServiceWithBody() throws Exception {
        mockMvc.perform(post("/users/{userId}/requests", 1)
                        .param("eventId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(requestService, Mockito.times(1))
                .createParticipationRequest(1L, 1L);
    }


    // cancelParticipationRequest
    @Test
    public void shouldCallCancelParticipationRequestAndCallServiceWithBody() throws Exception {
        mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", 1, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(requestService, Mockito.times(1))
                .cancelParticipationRequest(1L, 1L);
    }
}
