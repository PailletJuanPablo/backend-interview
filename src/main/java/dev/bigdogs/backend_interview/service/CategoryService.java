package dev.bigdogs.backend_interview.service;

import dev.bigdogs.backend_interview.model.Category;
import dev.bigdogs.backend_interview.repository.CategoryRepository;
import dev.bigdogs.backend_interview.exception.CategoryNotFoundException;
import dev.bigdogs.backend_interview.exception.InvalidCategoryOperationException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Service layer for managing Category entities.
 *
 * This service provides operations to:
 * - Create root categories.
 * - Create subcategories under existing categories.
 * - Retrieve ancestors and descendants of a category.
 * - Update the 'active' state of a subcategory.
 * - Delete categories (and their subcategories).
 *
 * Validations:
 * - Unique category name under the same parent.
 * - No cycles in the hierarchy.
 * - 'active' can only be updated in subcategories, not in root categories.
 */
@Service
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

    private static final String ERROR_ROOT_CATEGORY_EXISTS = "A root category with the given name already exists.";
    private static final String ERROR_PARENT_NOT_FOUND = "Parent category not found.";
    private static final String ERROR_CATEGORY_NOT_FOUND = "Category not found.";
    private static final String ERROR_ACTIVE_ON_ROOT = "'active' state cannot be updated on a root category.";
    private static final String ERROR_DUPLICATE_NAME_UNDER_PARENT = "A subcategory with the given name already exists under the specified parent.";

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new root category.
     *
     * @param name the root category name
     * @return the created category
     */
    @Transactional
    public Category createRootCategory(final String name) {
        LOGGER.debug("Creating a new root category with name: {}", name);

        if (categoryRepository.existsByNameAndParentIsNull(name)) {
            LOGGER.error(ERROR_ROOT_CATEGORY_EXISTS);
            throw new InvalidCategoryOperationException(ERROR_ROOT_CATEGORY_EXISTS);
        }

        Category category = new Category();
        category.setName(name);
        category.setActive(null);

        Category savedCategory = categoryRepository.save(category);
        LOGGER.info("Root category created with id: {}", savedCategory.getId());
        return savedCategory;
    }

    /**
     * Creates a new subcategory under an existing parent category.
     * The subcategory will be active by default.
     *
     * @param name the subcategory name
     * @param parentId the ID of the parent category
     * @return the created subcategory
     */
    @Transactional
    public Category createSubcategory(final String name, final Long parentId) {
        LOGGER.debug("Creating subcategory '{}' under parent with id: {}", name, parentId);

        Category parent = categoryRepository.findById(parentId)
            .orElseThrow(() -> new CategoryNotFoundException(ERROR_PARENT_NOT_FOUND));

        if (categoryRepository.existsByNameAndParent(name, parent)) {
            LOGGER.error(ERROR_DUPLICATE_NAME_UNDER_PARENT);
            throw new InvalidCategoryOperationException(ERROR_DUPLICATE_NAME_UNDER_PARENT);
        }

        Category subcategory = new Category();
        subcategory.setName(name);
        subcategory.setParent(parent);
        subcategory.setActive(Boolean.TRUE);

        Category savedSubcategory = categoryRepository.save(subcategory);
        LOGGER.info("Subcategory created with id: {} under parent id: {}", savedSubcategory.getId(), parentId);
        return savedSubcategory;
    }

    /**
     * Retrieves all ancestors and descendants of a specific category.
     *
     * @param categoryId the ID of the category
     * @return a list containing ancestors, the category itself, and its descendants
     */
    @Transactional(readOnly = true)
    public List<Category> getAncestorsAndDescendants(final Long categoryId) {
        LOGGER.debug("Retrieving ancestors and descendants for category id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(ERROR_CATEGORY_NOT_FOUND));

        List<Category> ancestors = getAncestors(category);
        List<Category> descendants = getDescendants(category);

        List<Category> result = new ArrayList<>();
        result.addAll(ancestors);
        result.add(category);
        result.addAll(descendants);

        LOGGER.debug("Ancestors and descendants retrieved for category id: {}", categoryId);
        return result;
    }

    /**
     * Updates the 'active' state of a subcategory.
     *
     * @param categoryId the ID of the subcategory
     * @param active the new active state
     * @return the updated category
     */
    @Transactional
    public Category updateActiveState(final Long categoryId, final Boolean active) {
        LOGGER.debug("Updating 'active' state for category id: {} to {}", categoryId, active);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(ERROR_CATEGORY_NOT_FOUND));

        if (category.getParent() == null) {
            LOGGER.error(ERROR_ACTIVE_ON_ROOT);
            throw new InvalidCategoryOperationException(ERROR_ACTIVE_ON_ROOT);
        }

        category.setActive(active);
        Category updatedCategory = categoryRepository.save(category);
        LOGGER.info("'active' state updated for category id: {} to {}", categoryId, active);
        return updatedCategory;
    }

    /**
     * Deletes a category. Subcategories are deleted automatically due to cascading.
     *
     * @param categoryId the ID of the category to delete
     */
    @Transactional
    public void deleteCategory(final Long categoryId) {
        LOGGER.debug("Deleting category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(ERROR_CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
        LOGGER.info("Category with id: {} deleted successfully.", categoryId);
    }

    /**
     * Retrieves all ancestors of the given category, starting from its parent and moving up to the root.
     */
    private List<Category> getAncestors(final Category category) {
        List<Category> ancestors = new ArrayList<>();
        Category current = category.getParent();
        while (current != null) {
            ancestors.add(0, current);
            current = current.getParent();
        }
        return ancestors;
    }

    /**
     * Recursively retrieves all descendants of the given category.
     */
    private List<Category> getDescendants(final Category category) {
        List<Category> descendants = new ArrayList<>();
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            for (Category sub : category.getSubcategories()) {
                descendants.add(sub);
                descendants.addAll(getDescendants(sub));
            }
        }
        return descendants;
    }
}
