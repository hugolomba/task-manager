package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController // @controller and @ResponseBody
@RequestMapping("/api/v1/tasks") // base path
public class TaskController {

//    temporary in-memory task list
//    private List<Task> tasks = new ArrayList<>();
//    private Long nextId = 1L;

    private final TaskRepository taskRepository;

    // Dependency Injection of the task repository
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById (@PathVariable Long id) {
        return taskRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    //@RequestBody transform the JSON coming in the request body in a Java object
    public ResponseEntity<Task> createTask(@RequestBody Task task)  {
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

// version without stream
//    @PutMapping("/{id}")
//    public Task updateTask (@PathVariable Long id, @RequestBody Task updatedTask) {
//
//        for (int i = 0; i < tasks.size(); i++) {
//            Task task = tasks.get(i);
//
//            if (task.getId().equals(id)) {
//                updatedTask.setId(id);
//                updatedTask.setCreatedAt(task.getCreatedAt());
//                tasks.set(i, updatedTask);
//                return updatedTask;
//            }
//        }
//
//        return null;
//    }

//    @PutMapping("/{id}")
//    public Task updateTask (@PathVariable Long id, @RequestBody Task updatedTask) {
//        Task foundTask = tasks.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);
//
//        updatedTask.setId(id);
//        updatedTask.setCreatedAt(foundTask.getCreatedAt());
//
//        int index = tasks.indexOf(foundTask);
//
//        tasks.set(index, updatedTask);
//
//        return updatedTask;
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask (@PathVariable Long id, @RequestBody Task updatedTask) {

        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setCompleted((updatedTask.getCompleted()));
                    Task savedTask = taskRepository.save(task);
                    return ResponseEntity.ok(savedTask);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
