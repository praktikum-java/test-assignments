package ru.practicum.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.categories.controller.admin.CategoryAdminController;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.service.admin.CategoryAdminService;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryAdminController.class)

public class CategoryAdminControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryAdminService adminService;
    NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("Sport").build();
    CategoryDto categoryDto = CategoryDto.builder().id(1L).name("Sport").build();

    // createCategory
    @Test
    public void shouldCreateCategoryAndCallServiceWithBody() throws Exception {
        when(adminService.createCategory(newCategoryDto)).thenReturn(categoryDto);
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sport"));
        Mockito.verify(adminService, Mockito.times(1))
                .createCategory(newCategoryDto);
    }

    // changeCategory
    @Test
    public void shouldUpdateCategoryAndCallServiceWithBody() throws Exception {
        when(adminService.changeCategory(categoryDto)).thenReturn(categoryDto);
        mockMvc.perform(patch("/admin/categories")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sport"));
        Mockito.verify(adminService, Mockito.times(1))
                .changeCategory(categoryDto);
    }

    @Test
    public void shouldRemoveCategoryAndCallServiceWithBody() throws Exception {
        mockMvc.perform(delete("/admin/categories/{catId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(adminService, Mockito.times(1))
                .removeCategory(1L);
    }
}
