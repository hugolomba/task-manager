package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.TaskRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.entity.Category;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.CategoryNotFoundException;
import com.hugo.taskmanager.exception.TaskNotFoundException;
import com.hugo.taskmanager.exception.UserNotFoundException;
import com.hugo.taskmanager.mapper.TaskMapper;
import com.hugo.taskmanager.repository.CategoryRepository;
import com.hugo.taskmanager.repository.TaskRepository;
import com.hugo.taskmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<TaskResponse> searchTasksByTitle(String username, String title, Pageable pageable) {
        return taskRepository.findByUserUsernameAndTitleContainingIgnoreCase(username, title, pageable)
                .map(taskMapper::toResponse);
    }

    public Page<TaskResponse> searchTasksByTitleAndCompletion(String username, String title, Boolean completed, Pageable pageable) {
        return taskRepository.findByUserUsernameAndTitleContainingIgnoreCaseAndCompleted(username, title, completed, pageable)
                .map(taskMapper::toResponse);
    }

    public Page<TaskResponse> getTasksByCompletion(String username, Boolean completed, Pageable pageable) {
        return taskRepository.findByUserUsernameAndCompleted(username, completed, pageable)
                .map(taskMapper::toResponse);
    }

    public List<TaskResponse> getAllTasks(String username) {
        return taskRepository.findByUserUsername(username)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public Page<TaskResponse> getAllTasks(String username, Pageable pageable) {
        return taskRepository.findByUserUsername(username, pageable)
                .map(taskMapper::toResponse);
    }


    public TaskResponse getTaskById(String username, Long id) {
        Task retrieveTask = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toResponse(retrieveTask);
    }

    public TaskResponse createTask(String username, TaskRequest task) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Category category = null;
        if (task.categoryId() != null) {
            category = categoryRepository.findById(task.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(task.categoryId()));
        }


        Task entityTask = taskMapper.toEntity(task, user, category);
        Task savedTask = taskRepository.save(entityTask);
        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTask(String username, Long id, TaskRequest updatedTask) {

        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.updateEntityFromRequest(task, updatedTask);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(String username, Long id) {

        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }

    public List<TaskResponse> getTasksByCompletionStatus(String username, boolean status) {
        List<Task> tasks = taskRepository.findByUserUsernameAndCompleted(username, status);

        return taskMapper.toResponseList(tasks);
    }

    public List<TaskResponse> searchTasksByTitle(String username, String title) {
        List<Task> tasks = taskRepository.findByUserUsernameAndTitleContainingIgnoreCase(username, title);

        return taskMapper.toResponseList(tasks);
    }
}
