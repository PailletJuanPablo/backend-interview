
package dev.bigdogs.backend_interview.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import dev.bigdogs.backend_interview.model.Category;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Test finding category by name and parent")
    public void testFindByNameAndParent() {
        Category parent = new Category();
        parent.setName("Electronics");
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Mobile Phones");
        child.setParent(parent);
        categoryRepository.save(child);

        Optional<Category> found = categoryRepository.findByNameAndParent("Mobile Phones", parent);
        assertTrue(found.isPresent());
        assertEquals("Mobile Phones", found.get().getName());
        assertEquals(parent.getId(), found.get().getParent().getId());
    }

    @Test
    @DisplayName("Test existsByNameAndParentIsNull")
    public void testExistsByNameAndParentIsNull() {
        Category root = new Category();
        root.setName("Books");
        categoryRepository.save(root);

        boolean exists = categoryRepository.existsByNameAndParentIsNull("Books");
        assertTrue(exists);

        boolean notExists = categoryRepository.existsByNameAndParentIsNull("Clothing");
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Test existsByNameAndParent")
    public void testExistsByNameAndParent() {
        Category parent = new Category();
        parent.setName("Home");
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Furniture");
        child.setParent(parent);
        categoryRepository.save(child);

        boolean exists = categoryRepository.existsByNameAndParent("Furniture", parent);
        assertTrue(exists);

        boolean notExists = categoryRepository.existsByNameAndParent("Appliances", parent);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Test findByParent")
    public void testFindByParent() {
        Category parent = new Category();
        parent.setName("Garden");
        categoryRepository.save(parent);

        Category child1 = new Category();
        child1.setName("Plants");
        child1.setParent(parent);
        categoryRepository.save(child1);

        Category child2 = new Category();
        child2.setName("Tools");
        child2.setParent(parent);
        categoryRepository.save(child2);

        List<Category> subcategories = categoryRepository.findByParent(parent);
        assertEquals(2, subcategories.size());
        assertTrue(subcategories.stream().anyMatch(c -> c.getName().equals("Plants")));
        assertTrue(subcategories.stream().anyMatch(c -> c.getName().equals("Tools")));
    }

    @Test
    @DisplayName("Test findByParentIsNull")
    public void testFindByParentIsNull() {
        Category root1 = new Category();
        root1.setName("Sports");
        categoryRepository.save(root1);

        Category root2 = new Category();
        root2.setName("Music");
        categoryRepository.save(root2);

        List<Category> roots = categoryRepository.findByParentIsNull();
        assertEquals(2, roots.size());
        assertTrue(roots.stream().anyMatch(c -> c.getName().equals("Sports")));
        assertTrue(roots.stream().anyMatch(c -> c.getName().equals("Music")));
    }
}