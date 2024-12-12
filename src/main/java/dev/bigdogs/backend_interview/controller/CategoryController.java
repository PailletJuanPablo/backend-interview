package dev.bigdogs.backend_interview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.bigdogs.backend_interview.dto.CategoryDTO;
import dev.bigdogs.backend_interview.dto.CategoryTreeDTO;
import dev.bigdogs.backend_interview.dto.CreateCategoryDTO;
import dev.bigdogs.backend_interview.dto.UpdateCategoryDTO;
import dev.bigdogs.backend_interview.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/root")
    public CategoryDTO createRootCategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        return categoryService.createRootCategory(createCategoryDTO);
    }

    @PostMapping("/sub")
    public CategoryDTO createSubcategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        return categoryService.createSubcategory(createCategoryDTO);
    }

    @GetMapping("/{id}/tree")
    public CategoryTreeDTO getAncestorsAndDescendants(@PathVariable Long id) {
        return categoryService.getAncestorsAndDescendants(id);
    }

    @PutMapping("/{id}/active")
    public CategoryDTO updateActiveState(@PathVariable Long id, @RequestBody UpdateCategoryDTO updateCategoryDTO) {
        return categoryService.updateActiveState(id, updateCategoryDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}