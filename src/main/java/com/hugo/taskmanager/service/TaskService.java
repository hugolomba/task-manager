package com.hugo.taskmanager.service;

import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.exception.TaskNotFoundException;
import com.hugo.taskmanager.repository.TaskRepository;
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

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask (Long id, Task updatedTask) {


        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted((updatedTask.getCompleted()));
        return taskRepository.save(task);


    }

    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }

    public List<Task> gateTasksByCompletionStatus(boolean status) {
        return taskRepository.findByCompleted(status);
    }

    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Task> getTasksByCompletionStatus(@RequestParam boolean completed) {
        return taskRepository.findTasksByCompletionStatus(completed);
    }


}
