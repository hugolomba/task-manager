package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.CategoryRequest;
import com.hugo.taskmanager.dto.CategoryResponse;
import com.hugo.taskmanager.entity.Category;
import com.hugo.taskmanager.exception.CategoryNotFoundException;
import com.hugo.taskmanager.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createsCategoryResponseFromRequest() {
        Category savedCategory = new Category(1L, "Work", "Tasks related to work");
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.create(new CategoryRequest("Work", "Tasks related to work"));

        assertEquals(1L, response.categoryId());
        assertEquals("Work", response.name());
        assertEquals("Tasks related to work", response.description());
    }

    @Test
    void throwsWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(99L));
    }
}
