package dev.bigdogs.backend_interview.controller;

import dev.bigdogs.backend_interview.dto.CreateCategoryRequest;
import dev.bigdogs.backend_interview.dto.CreateSubcategoryRequest;
import dev.bigdogs.backend_interview.dto.UpdateActiveRequest;
import dev.bigdogs.backend_interview.dto.CategoryResponse;
import dev.bigdogs.backend_interview.model.Category;
import dev.bigdogs.backend_interview.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing categories.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/root")
    public ResponseEntity<CategoryResponse> createRootCategory(@RequestBody CreateCategoryRequest request) {
        Category created = categoryService.createRootCategory(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PostMapping("/subcategory")
    public ResponseEntity<CategoryResponse> createSubcategory(@RequestBody CreateSubcategoryRequest request) {
        Category created = categoryService.createSubcategory(request.getName(), request.getParentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}/hierarchy")
    public ResponseEntity<List<CategoryResponse>> getAncestorsAndDescendants(@PathVariable Long id) {
        List<Category> hierarchy = categoryService.getAncestorsAndDescendants(id);
        List<CategoryResponse> response = hierarchy.stream().map(category -> toResponse(category)).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<CategoryResponse> updateActiveState(@PathVariable Long id, @RequestBody UpdateActiveRequest request) {
        Category updated = categoryService.updateActiveState(id, request.getActive());
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setActive(category.getActive());
        response.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        return response;
    }
}
