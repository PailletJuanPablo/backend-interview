
package dev.bigdogs.backend_interview.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UpdateCategoryDTOTest {

    @Test
    public void testUpdateCategoryDTO() {
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setActive(true);

        assertTrue(dto.getActive());
    }

    @Test
    public void testUpdateCategoryDTONoActive() {
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setActive(false);

        assertFalse(dto.getActive());
    }

}