package com.hugo.taskmanager.mapper;

import com.hugo.taskmanager.dto.*;
import com.hugo.taskmanager.entity.Category;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.service.CategoryService;
import com.hugo.taskmanager.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;


// This is used to convert a DTO to an Entity

@Component
public class TaskMapper {

    private final CategoryService categoryService;
    private final UserService userService;
    private final UserMapper userMapper;

    public TaskMapper(CategoryService categoryService, UserService userService, UserMapper userMapper) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public Task toEntity(TaskRequest request) {

        Category category = null;
        User user = null;

        System.out.println(request.categoryId());
        System.out.println(request.userId());

        if (request != null && request.categoryId() != null) {
            category = categoryService.findById(request.categoryId());
        }

        if (request != null && request.userId() != null) {
            user = userService.getUserById(request.userId());
        }

        return Task.builder()
                .title(request.title())
                .description(request.description())
                .completed(request.completed() != null ? request.completed() : false)
                .category(category)
                .user(user)
                .build();
    }

    public TaskResponse toResponse(Task task) {
        CategoryResponse categoryResponse = null;
        UserResponse userResponse = null;

        if (task != null && task.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .categoryId(task.getCategory().getId())
                    .name(task.getCategory().getName())
                    .description(task.getCategory().getDescription())
                    .build();
        }

        if (task != null && task.getUser() != null) {
            userResponse = UserResponse.builder().id(task.getUser().getId()).name(task.getUser().getName()).surname(task.getUser().getSurname()).username(task.getUser().getUsername()).createdAt(task.getUser().getCreatedAt()).build();
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .category(categoryResponse)
                .user(userResponse)
                .build();
    }

    public List<TaskResponse> toResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toResponse) // converts each Task to TaskResponse
                .toList();
    }

    public void updateEntityFromRequest(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());

        if (request.categoryId() != null) {
            task.setCategory(categoryService.findById(request.categoryId()));
        }
    }
}
