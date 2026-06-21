package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.CategoryRequest;
import com.hugo.taskmanager.dto.CategoryResponse;
import com.hugo.taskmanager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    public final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("{id}")
    public CategoryResponse findById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest category
    ) {
        CategoryResponse create = categoryService.create(category);
        return  ResponseEntity.status(HttpStatus.CREATED).body(create);
    }
}
