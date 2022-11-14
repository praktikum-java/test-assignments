package ru.practicum.compilations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.compilations.controller.admin.CompilationAdminController;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.service.admin.CompilationAdminService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompilationAdminController.class)
public class CompilationAdminControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompilationAdminService compilationAdminService;
    NewCompilationDto newCompilationDto = NewCompilationDto.builder()
            .events(List.of(1L, 2L)).pinned(true).title("Weekend").build();
    CompilationDto compilationDto = CompilationDto.builder()
            .id(1L).events(List.of()).pinned(true).title("Weekend").build();

    // createNewCompilation
    @Test
    public void shouldCreateCompilationAndCallServiceWithBody() throws Exception {
        when(compilationAdminService.createNewCompilation(newCompilationDto)).thenReturn(compilationDto);
        mockMvc.perform(post("/admin/compilations")
                        .content(objectMapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Weekend"));
        Mockito.verify(compilationAdminService, Mockito.times(1))
                .createNewCompilation(newCompilationDto);
    }

    // removeCompilation
    @Test
    public void shouldCallRemoveCompilation() throws Exception {
        mockMvc.perform(delete("/admin/compilations/{compId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationAdminService, Mockito.times(1))
                .removeCompilation(1L);
    }

    // removeEventFromCompilation
    @Test
    public void shouldCallRemoveEventFromCompilation() throws Exception {
        mockMvc.perform(delete("/admin/compilations/{compId}/events/{eventId}", 1, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationAdminService, Mockito.times(1))
                .removeEventFromCompilation(1L, 1L);
    }

    // addEventInCompilation
    @Test
    public void shouldCallAddEventInCompilation() throws Exception {
        mockMvc.perform(patch("/admin/compilations/{compId}/events/{eventId}", 1, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationAdminService, Mockito.times(1))
                .addEventInCompilation(1L, 1L);
    }

    // unpinCompilation
    @Test
    public void shouldCallUnpinCompilation() throws Exception {
        mockMvc.perform(delete("/admin/compilations/{compId}/pin", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationAdminService, Mockito.times(1))
                .unpinCompilation(1L);
    }

    // pinCompilation
    @Test
    public void shouldCallPinCompilation() throws Exception {
        mockMvc.perform(patch("/admin/compilations/{compId}/pin", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(compilationAdminService, Mockito.times(1))
                .pinCompilation(1L);
    }

}
