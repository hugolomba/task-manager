
package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.TaskRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // @controller and @ResponseBody
@RequestMapping("/api/v1/tasks") // base path
public class TaskController {

    private final TaskService taskService;

    // Dependency Injection of the task service
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


//    Get all tasks
    @GetMapping("/all")
    public List<TaskResponse> getAllTasks(Authentication authentication) {
        return taskService.getAllTasks(authentication.getName());
    }

//    Get task by id
    @GetMapping("/{id}")
    public TaskResponse getTaskById(Authentication authentication, @PathVariable Long id) {
    return taskService.getTaskById(authentication.getName(), id);
}

//  Create new task
    @PostMapping
    //@RequestBody transform the JSON coming in the request body in a Java object
    public ResponseEntity<TaskResponse> createTask(Authentication authentication, @Valid @RequestBody TaskRequest task)  {
        TaskResponse savedTask = taskService.createTask(authentication.getName(), task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

//    Filtering
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTasks(
            Authentication authentication,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir

    ) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TaskResponse> taskPage;
        String username = authentication.getName();

        if (title != null && completed != null) {
            // Filter by both
            taskPage = taskService.searchTasksByTitleAndCompletion(username, title, completed, pageable);

        } else if (title != null) {
            // Filter by title only
            taskPage = taskService.searchTasksByTitle(username, title, pageable);
        } else if (completed != null) {
            // Filter completion only
            taskPage = taskService.getTasksByCompletion(username, completed, pageable);
        } else {
            taskPage = taskService.getAllTasks(username, pageable);
        }

        return new ResponseEntity<>(buildPageResponse(taskPage), HttpStatus.OK);

    }

    // getAllTasks paginated version
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

//        sort
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResponse> taskPage = taskService.getAllTasks(authentication.getName(), pageable);

        return new ResponseEntity<>(buildPageResponse(taskPage), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public TaskResponse updateTask(Authentication authentication, @PathVariable Long id, @Valid @RequestBody TaskRequest updatedTask) {

        return taskService.updateTask(authentication.getName(), id, updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable Long id) {

        taskService.deleteTask(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/completed/{status}")
    public List<TaskResponse> getTasksByCompletions(Authentication authentication, @PathVariable boolean status) {
        return taskService.getTasksByCompletionStatus(authentication.getName(), status);
    }

    @GetMapping("/search-by-title")
    public List<TaskResponse> searchTasksByTitle(Authentication authentication, @RequestParam String title) {
        return taskService.searchTasksByTitle(authentication.getName(), title);
    }

    @GetMapping("/completed")
    public List<TaskResponse> getTasksByCompletionStatus(Authentication authentication, @RequestParam boolean completed) {
        return taskService.getTasksByCompletionStatus(authentication.getName(), completed);
    }

    private Map<String, Object> buildPageResponse(Page<TaskResponse> taskPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("tasks", taskPage.getContent());
        response.put("currentPage", taskPage.getNumber());
        response.put("totalItems", taskPage.getTotalElements());
        response.put("totalPages", taskPage.getTotalPages());
        response.put("hasNext", taskPage.hasNext());
        response.put("hasPrevious", taskPage.hasPrevious());

        return response;
    }

}
