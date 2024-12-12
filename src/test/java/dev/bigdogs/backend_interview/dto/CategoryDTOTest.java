
package dev.bigdogs.backend_interview.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CategoryDTOTest {

    @Test
    public void testCategoryDTO() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setName("Electronics");
        dto.setActive(true);
        dto.setParentId(null);

        assertEquals(1L, dto.getId());
        assertEquals("Electronics", dto.getName());
        assertTrue(dto.getActive());
        assertNull(dto.getParentId());
    }

    @Test
    public void testCategoryDTONullActive() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(2L);
        dto.setName("Books");
        dto.setActive(null);
        dto.setParentId(1L);

        assertEquals(2L, dto.getId());
        assertEquals("Books", dto.getName());
        assertNull(dto.getActive());
        assertEquals(1L, dto.getParentId());
    }

}