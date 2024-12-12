package dev.bigdogs.backend_interview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import dev.bigdogs.backend_interview.dto.CategoryDTO;
import dev.bigdogs.backend_interview.dto.CreateCategoryDTO;
import dev.bigdogs.backend_interview.dto.UpdateCategoryDTO;
import dev.bigdogs.backend_interview.service.CategoryService;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private CategoryService categoryService; // Added MockBean for CategoryService

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateRootCategory_Success() throws Exception {
        CreateCategoryDTO createDto = new CreateCategoryDTO();
        createDto.setName("Electronics");

        CategoryDTO responseDto = new CategoryDTO();
        responseDto.setId(1L);
        responseDto.setName("Electronics");
        responseDto.setActive(null);
        responseDto.setParentId(null);

        when(categoryService.createRootCategory(ArgumentMatchers.<CreateCategoryDTO>any())).thenReturn(responseDto);

        mockMvc.perform(post("/categories/root")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Electronics")))
            .andExpect(jsonPath("$.active").doesNotExist())
            .andExpect(jsonPath("$.parentId").doesNotExist());

        verify(categoryService, times(1)).createRootCategory(ArgumentMatchers.<CreateCategoryDTO>any());
    }

    @Test
    public void testCreateSubcategory_Success() throws Exception {
        CreateCategoryDTO createDto = new CreateCategoryDTO();
        createDto.setName("Mobile Phones");
        createDto.setParentId(1L);

        CategoryDTO responseDto = new CategoryDTO();
        responseDto.setId(2L);
        responseDto.setName("Mobile Phones");
        responseDto.setActive(true);
        responseDto.setParentId(1L);

        when(categoryService.createSubcategory(ArgumentMatchers.<CreateCategoryDTO>any())).thenReturn(responseDto);

        mockMvc.perform(post("/categories/sub")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.name", is("Mobile Phones")))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.parentId", is(1)));

        verify(categoryService, times(1)).createSubcategory(ArgumentMatchers.<CreateCategoryDTO>any());
    }

    @Test
    public void testGetAncestorsAndDescendants_Success() throws Exception {
        Long categoryId = 1L;

        // Assuming CategoryTreeDTO is properly structured
        mockMvc.perform(get("/categories/{id}/tree", categoryId))
            .andExpect(status().isOk());

        verify(categoryService, times(1)).getAncestorsAndDescendants(categoryId);
    }

    @Test
    public void testUpdateActiveState_Success() throws Exception {
        Long categoryId = 2L;
        UpdateCategoryDTO updateDto = new UpdateCategoryDTO();
        updateDto.setActive(false);

        CategoryDTO responseDto = new CategoryDTO();
        responseDto.setId(2L);
        responseDto.setName("Mobile Phones");
        responseDto.setActive(false);
        responseDto.setParentId(1L);

        when(categoryService.updateActiveState(eq(categoryId), ArgumentMatchers.<UpdateCategoryDTO>any())).thenReturn(responseDto);

        mockMvc.perform(put("/categories/{id}/active", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.name", is("Mobile Phones")))
            .andExpect(jsonPath("$.active", is(false)))
            .andExpect(jsonPath("$.parentId", is(1)));

        verify(categoryService, times(1)).updateActiveState(eq(categoryId), ArgumentMatchers.<UpdateCategoryDTO>any());
    }

    @Test
    public void testDeleteCategory_Success() throws Exception {
        Long categoryId = 2L;

        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId))
            .andExpect(status().isOk());

        verify(categoryService, times(1)).deleteCategory(categoryId);
    }

}