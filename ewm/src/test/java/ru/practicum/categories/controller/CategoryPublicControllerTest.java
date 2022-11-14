package ru.practicum.categories.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.categories.controller.publ.CategoryPublicController;
import ru.practicum.categories.service.publ.CategoryPublicService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryPublicController.class)

public class CategoryPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryPublicService categoryPublicService;

    // getCategories
    @Test
    public void shouldCallServiceGetCategories() throws Exception {
        mockMvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(categoryPublicService, Mockito.times(1))
                .getCategories(0, 10);
    }


    // getCategoryById
    @Test
    public void shouldCallServiceGetCategoryById() throws Exception {
        mockMvc.perform(get("/categories/{catId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(categoryPublicService, Mockito.times(1))
                .getCategoryById(1L);
    }
}
