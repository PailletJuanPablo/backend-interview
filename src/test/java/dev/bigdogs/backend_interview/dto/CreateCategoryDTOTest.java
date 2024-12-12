
package dev.bigdogs.backend_interview.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CreateCategoryDTOTest {

    @Test
    public void testCreateCategoryDTO() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Clothing");
        dto.setParentId(1L);

        assertEquals("Clothing", dto.getName());
        assertEquals(1L, dto.getParentId());
    }

    @Test
    public void testCreateCategoryDTONullParent() {
        CreateCategoryDTO dto = new CreateCategoryDTO();
        dto.setName("Accessories");
        dto.setParentId(null);

        assertEquals("Accessories", dto.getName());
        assertNull(dto.getParentId());
    }

}