package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.entity.Category;
import com.hugo.taskmanager.service.CategoryService;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    public final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

//    @GetMapping
//    public List<Category> getAll() {
//        return categoryService.getAll();
//    }

    @GetMapping
    public Category findById(Long id) {
        return categoryService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Category> create(
            @RequestBody Category category
    ) {
        Category create = categoryService.create(category);
        return  ResponseEntity.status(HttpStatus.CREATED).body(create);
    }
}
