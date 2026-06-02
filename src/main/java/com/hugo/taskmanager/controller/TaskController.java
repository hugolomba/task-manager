
package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.TaskRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController // @controller and @ResponseBody
@RequestMapping("/api/v1/tasks") // base path
public class TaskController {

    private final TaskService taskService;

    // Dependency Injection of the task service
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

//    @GetMapping
//    public List<Task> getAllTasks() {
//        return taskService.getAllTasks();
//    }

    // getAllTasks paginated version

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

//        sort
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskService.getAllTasks(pageable);

        List<TaskResponse> tasks = taskPage.getContent()
                .stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getCompleted(),
                        task.getCreatedAt()))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);
        response.put("currentPage", taskPage.getNumber());
        response.put("totalItems", taskPage.getTotalElements());
        response.put("totalPages", taskPage.getTotalPages());
        response.put("hasNext", taskPage.hasNext());
        response.put("hasPrevious", taskPage.hasPrevious());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById (@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    //@RequestBody transform the JSON coming in the request body in a Java object
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest task)  {
        TaskResponse savedTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }


    @PutMapping("/{id}")
    public TaskResponse updateTask (@PathVariable Long id, @Valid @RequestBody TaskRequest updatedTask) {

        return taskService.updateTask(id, updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/completed/{status}")
    public List<TaskResponse> getTasksByCompletions(@PathVariable boolean status) {
        return taskService.getTasksByCompletionStatus(status);
    }

    @GetMapping("/search")
    public List<TaskResponse> searchTasksByTitle(@RequestParam String title) {
        return taskService.searchTasksByTitle(title);
    }

    @GetMapping("/completed")
    public List<TaskResponse> getTasksByCompletionStatus(@RequestParam boolean completed) {
        return taskService.getTasksByCompletionStatus(completed);
    }

}
