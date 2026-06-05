package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.TaskRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.exception.TaskNotFoundException;
import com.hugo.taskmanager.mapper.TaskMapper;
import com.hugo.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Page<Task> searchTasksByTitle(String title, Pageable pageable) {
        return taskRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Task> searchTasksByTitleAndCompletion(String title, Boolean completed, Pageable pageable) {
        return taskRepository.findByTitleContainingAndCompleted(title, completed, pageable);
    }

    public Page<Task> getTasksByCompletion(Boolean completed, Pageable pageable) {
        return taskRepository.findByCompleted(completed, pageable);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // paginated version of getAllTasks
    public Page<Task> getAllTasks(Pageable pageable) {

        return taskRepository.findAll(pageable);
    }


    public TaskResponse getTaskById(Long id) {
        Task retrieveTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toResponse(retrieveTask);
    }

    public TaskResponse createTask(TaskRequest task) {
        Task entityTask = taskMapper.toEntity(task);
        Task savedTask = taskRepository.save(entityTask);
        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTask (Long id, TaskRequest updatedTask) {

        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.updateEntityFromRequest(task, updatedTask);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }

    public List<TaskResponse> getTasksByCompletionStatus(boolean status) {
        List<Task> tasks = taskRepository.findTasksByCompletionStatus(status);

        return taskMapper.toResponseList(tasks);
    }

    // paginated version of getTaskByCompletionStatus

    public Page<TaskResponse> gateTasksByCompletionStatus(boolean status, Pageable pageable) {

        final Page<Task> completedTasks = (Page<Task>) taskRepository.findTasksByCompletionStatus(status, pageable);

        return completedTasks.map(taskMapper::toResponse);
    }

    public List<TaskResponse> searchTasksByTitle(String title) {
        List<Task> tasks = taskRepository.findByTitleContainingIgnoreCase(title);

        return taskMapper.toResponseList(tasks);
    }
//
//    public List<TaskResponse> getTasksByCompletionStatus(@RequestParam boolean completed) {
//        List<Task> tasks = taskRepository.findTasksByCompletionStatus(completed);
//
//        return taskMapper.toResponseList(tasks);
//    }


}
