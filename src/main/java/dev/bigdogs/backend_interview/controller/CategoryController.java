
package dev.bigdogs.backend_interview.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import dev.bigdogs.backend_interview.service.CategoryService;
import dev.bigdogs.backend_interview.model.Category;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/root")
    public Category createRootCategory(@RequestParam String name) {
        return categoryService.createRootCategory(name);
    }

    @PostMapping("/sub")
    public Category createSubcategory(@RequestParam String name, @RequestParam Long parentId) {
        return categoryService.createSubcategory(name, parentId);
    }

    @GetMapping("/{id}/tree")
    public List<Category> getAncestorsAndDescendants(@PathVariable Long id) {
        return categoryService.getAncestorsAndDescendants(id);
    }

    @PutMapping("/{id}/active")
    public Category updateActiveState(@PathVariable Long id, @RequestParam Boolean active) {
        return categoryService.updateActiveState(id, active);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}