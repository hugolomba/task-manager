package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.CategoryRequest;
import com.hugo.taskmanager.dto.CategoryResponse;
import com.hugo.taskmanager.entity.Category;
import com.hugo.taskmanager.exception.CategoryNotFoundException;
import com.hugo.taskmanager.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

    }

    public CategoryResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public CategoryResponse create(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());

        return toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
