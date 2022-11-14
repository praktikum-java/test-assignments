package ru.practicum.compilations.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.compilations.controller.publ.CompilationsPublicController;
import ru.practicum.compilations.service.publ.CompilationPublicService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompilationsPublicController.class)
public class CompilationsPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompilationPublicService compilationPublicService;

    // getCompilations
    @Test
    public void shouldCreateCompilationAndCallServiceWithBody() throws Exception {
        mockMvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationPublicService, Mockito.times(1))
                .getCompilations(true, 1, 20);
    }

    // getCompilationById
    @Test
    public void should() throws Exception {
        mockMvc.perform(get("/compilations/{compId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationPublicService, Mockito.times(1))
                .getCompilationById(1L);
    }
}
