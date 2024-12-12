package dev.bigdogs.backend_interview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.bigdogs.backend_interview.model.Category;

/**
 * Repository interface for managing Category entities.
 * 
 * Provides methods to:
 * - Find categories by name and parent.
 * - Check the existence of categories under specific conditions.
 * - Retrieve lists of categories by their parent.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name and parent.
     *
     * @param name the category name
     * @param parent the parent category
     * @return an optional containing the category if found, empty otherwise
     */
    Optional<Category> findByNameAndParent(String name, Category parent);

    /**
     * Checks if a root category (no parent) with the given name exists.
     *
     * @param name the category name
     * @return true if a root category with the given name exists, false otherwise
     */
    boolean existsByNameAndParentIsNull(String name);

    /**
     * Checks if a category with the given name and parent exists.
     *
     * @param name the category name
     * @param parent the parent category
     * @return true if a category with the given name and parent exists, false otherwise
     */
    boolean existsByNameAndParent(String name, Category parent);

    /**
     * Finds all categories that have the given parent.
     *
     * @param parent the parent category
     * @return a list of categories with the given parent
     */
    List<Category> findByParent(Category parent);

    /**
     * Finds all root categories (categories with no parent).
     *
     * @return a list of root categories
     */
    List<Category> findByParentIsNull();
}
