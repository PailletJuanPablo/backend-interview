package dev.bigdogs.backend_interview.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.bigdogs.backend_interview.dto.CategoryDTO;
import dev.bigdogs.backend_interview.dto.CategoryTreeDTO;
import dev.bigdogs.backend_interview.dto.CreateCategoryDTO;
import dev.bigdogs.backend_interview.dto.UpdateCategoryDTO;
import dev.bigdogs.backend_interview.exception.CategoryNotFoundException;
import dev.bigdogs.backend_interview.exception.InvalidCategoryOperationException;
import dev.bigdogs.backend_interview.model.Category;
import dev.bigdogs.backend_interview.repository.CategoryRepository;

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

    @Autowired
    private CategoryRepository categoryRepository; // Ensure this is properly injected

    @Autowired
    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new root category.
     *
     * @param createCategoryDTO the DTO containing category data
     * @return the created category DTO
     */
    @Transactional
    public CategoryDTO createRootCategory(final CreateCategoryDTO createCategoryDTO) {
        LOGGER.debug("Creating a new root category with name: {}", createCategoryDTO.getName());

        if (categoryRepository.existsByNameAndParentIsNull(createCategoryDTO.getName())) {
            LOGGER.error("A root category with the given name already exists.");
            throw new InvalidCategoryOperationException();
        }

        Category category = new Category();
        category.setName(createCategoryDTO.getName());
        category.setActive(null);

        Category savedCategory = categoryRepository.save(category);
        LOGGER.info("Root category created with id: {}", savedCategory.getId());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(savedCategory.getId());
        categoryDTO.setName(savedCategory.getName());
        categoryDTO.setActive(savedCategory.getActive());
        categoryDTO.setParentId(null);
        return categoryDTO;
    }

    /**
     * Creates a new subcategory under an existing parent category.
     * The subcategory will be active by default.
     *
     * @param createCategoryDTO the DTO containing subcategory data
     * @return the created subcategory DTO
     */
    @Transactional
    public CategoryDTO createSubcategory(final CreateCategoryDTO createCategoryDTO) {
        LOGGER.debug("Creating subcategory '{}' under parent with id: {}", createCategoryDTO.getName(), createCategoryDTO.getParentId());

        Category parent = categoryRepository.findById(createCategoryDTO.getParentId())
            .orElseThrow(CategoryNotFoundException::new);

        if (categoryRepository.existsByNameAndParent(createCategoryDTO.getName(), parent)) {
            LOGGER.error("A subcategory with the given name already exists under the specified parent.");
            throw new InvalidCategoryOperationException();
        }

        Category subcategory = new Category();
        subcategory.setName(createCategoryDTO.getName());
        subcategory.setParent(parent);
        subcategory.setActive(Boolean.TRUE);

        Category savedSubcategory = categoryRepository.save(subcategory);
        LOGGER.info("Subcategory created with id: {} under parent id: {}", savedSubcategory.getId(), createCategoryDTO.getParentId());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(savedSubcategory.getId());
        categoryDTO.setName(savedSubcategory.getName());
        categoryDTO.setActive(savedSubcategory.getActive());
        categoryDTO.setParentId(createCategoryDTO.getParentId());
        return categoryDTO;
    }

    /**
     * Retrieves all ancestors and descendants of a specific category.
     *
     * @param categoryId the ID of the category
     * @return a CategoryTreeDTO containing ancestors, the category itself, and its descendants
     */
    @Transactional(readOnly = true)
    public CategoryTreeDTO getAncestorsAndDescendants(final Long categoryId) {
        LOGGER.debug("Retrieving ancestors and descendants for category id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);

        CategoryTreeDTO treeDTO = mapToTreeDTO(category);
        LOGGER.debug("Ancestors and descendants retrieved for category id: {}", categoryId);
        return treeDTO;
    }

    /**
     * Maps a Category entity to CategoryTreeDTO recursively.
     *
     * @param category the Category entity
     * @return the mapped CategoryTreeDTO
     */
    private CategoryTreeDTO mapToTreeDTO(Category category) {
        CategoryTreeDTO dto = new CategoryTreeDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setActive(category.getActive());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);

        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            List<CategoryTreeDTO> subDTOs = new ArrayList<>();
            for (Category sub : category.getSubcategories()) {
                subDTOs.add(mapToTreeDTO(sub));
            }
            dto.setSubcategories(subDTOs);
        }

        return dto;
    }

    /**
     * Updates the 'active' state of a subcategory.
     *
     * @param categoryId the ID of the subcategory
     * @param updateCategoryDTO the DTO containing the new active state
     * @return the updated category DTO
     */
    @Transactional
    public CategoryDTO updateActiveState(final Long categoryId, final UpdateCategoryDTO updateCategoryDTO) {
        LOGGER.debug("Updating 'active' state for category id: {} to {}", categoryId, updateCategoryDTO.getActive());

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);

        if (category.getParent() == null) {
            LOGGER.error("'active' state cannot be updated on a root category.");
            throw new InvalidCategoryOperationException();
        }

        category.setActive(updateCategoryDTO.getActive());
        Category updatedCategory = categoryRepository.save(category);
        LOGGER.info("'active' state updated for category id: {} to {}", categoryId, updateCategoryDTO.getActive());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(updatedCategory.getId());
        categoryDTO.setName(updatedCategory.getName());
        categoryDTO.setActive(updatedCategory.getActive());
        categoryDTO.setParentId(updatedCategory.getParent() != null ? updatedCategory.getParent().getId() : null);
        return categoryDTO;
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
            .orElseThrow(CategoryNotFoundException::new);

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

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }
}
