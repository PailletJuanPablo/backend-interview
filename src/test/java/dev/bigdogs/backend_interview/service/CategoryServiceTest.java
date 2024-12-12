
package dev.bigdogs.backend_interview.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.bigdogs.backend_interview.dto.CategoryDTO;
import dev.bigdogs.backend_interview.dto.CreateCategoryDTO;
import dev.bigdogs.backend_interview.dto.UpdateCategoryDTO;
import dev.bigdogs.backend_interview.exception.CategoryNotFoundException;
import dev.bigdogs.backend_interview.exception.InvalidCategoryOperationException;
import dev.bigdogs.backend_interview.model.Category;
import dev.bigdogs.backend_interview.repository.CategoryRepository;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRootCategory_Success() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Electronics");

        when(categoryRepository.existsByNameAndParentIsNull("Electronics")).thenReturn(false);

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");
        savedCategory.setActive(null);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDTO result = categoryService.createRootCategory(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        assertNull(result.getActive());
        assertNull(result.getParentId());

        verify(categoryRepository, times(1)).existsByNameAndParentIsNull("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testCreateRootCategory_DuplicateName() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Electronics");

        when(categoryRepository.existsByNameAndParentIsNull("Electronics")).thenReturn(true);

        assertThrows(InvalidCategoryOperationException.class, () -> {
            categoryService.createRootCategory(dto);
        });

        verify(categoryRepository, times(1)).existsByNameAndParentIsNull("Electronics");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testCreateSubcategory_Success() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Mobile Phones");
        dto.setParentId(1L);

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.existsByNameAndParent("Mobile Phones", parent)).thenReturn(false);

        Category savedSubcategory = new Category();
        savedSubcategory.setId(2L);
        savedSubcategory.setName("Mobile Phones");
        savedSubcategory.setActive(true);
        savedSubcategory.setParent(parent);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedSubcategory);

        CategoryDTO result = categoryService.createSubcategory(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Mobile Phones", result.getName());
        assertTrue(result.getActive());
        assertEquals(1L, result.getParentId());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByNameAndParent("Mobile Phones", parent);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testCreateSubcategory_ParentNotFound() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Mobile Phones");
        dto.setParentId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.createSubcategory(dto);
        });

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).existsByNameAndParent(anyString(), any());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testUpdateActiveState_Success() {
        Long categoryId = 2L;
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setActive(false);

        Category subcategory = new Category();
        subcategory.setId(categoryId);
        subcategory.setName("Mobile Phones");
        subcategory.setActive(true);
        Category parent = new Category();
        parent.setId(1L);
        subcategory.setParent(parent);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(subcategory));
        when(categoryRepository.save(subcategory)).thenReturn(subcategory);

        CategoryDTO result = categoryService.updateActiveState(categoryId, dto);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Mobile Phones", result.getName());
        assertFalse(result.getActive());
        assertEquals(1L, result.getParentId());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(subcategory);
    }

    @Test
    public void testUpdateActiveState_RootCategory() {
        Long categoryId = 1L;
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setActive(false);

        Category rootCategory = new Category();
        rootCategory.setId(categoryId);
        rootCategory.setName("Electronics");
        rootCategory.setActive(null);
        rootCategory.setParent(null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(rootCategory));

        assertThrows(InvalidCategoryOperationException.class, () -> {
            categoryService.updateActiveState(categoryId, dto);
        });

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testDeleteCategory_Success() {
        Long categoryId = 2L;

        Category subcategory = new Category();
        subcategory.setId(categoryId);
        subcategory.setName("Mobile Phones");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(subcategory));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(subcategory);
    }

    @Test
    public void testDeleteCategory_NotFound() {
        Long categoryId = 3L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

}