package dev.bigdogs.backend_interview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import dev.bigdogs.backend_interview.dto.CreateCategoryDTO;
import dev.bigdogs.backend_interview.dto.UpdateCategoryDTO;
import dev.bigdogs.backend_interview.model.Category;
import dev.bigdogs.backend_interview.repository.CategoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testUpdateActiveState() throws Exception {
        // Create Root Category
        Category root = new Category();
        root.setName("Books");
        categoryRepository.save(root);

        // Create Subcategory
        Category sub = new Category();
        sub.setName("Fiction");
        sub.setParent(root);
        sub.setActive(true);
        categoryRepository.save(sub);

        // Update Active State
        UpdateCategoryDTO updateDto = new UpdateCategoryDTO();
        updateDto.setActive(false);

        mockMvc.perform(put("/categories/{id}/active", sub.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        // Create Root Category
        Category root = new Category();
        root.setName("Garden");
        categoryRepository.save(root);

        // Delete Category
        mockMvc.perform(delete("/categories/{id}", root.getId()))
            .andExpect(status().isOk());

        // Verify Deletion
        mockMvc.perform(get("/categories/{id}/tree", root.getId()))
            .andExpect(status().isNotFound());
    }

}