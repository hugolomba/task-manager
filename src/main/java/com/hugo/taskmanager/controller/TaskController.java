package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.entity.Task;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController // @controller and @ResponseBody
@RequestMapping("/api/v1/tasks") // base path
public class TaskController {

//    temporary in-memory task list
    private List<Task> tasks = new ArrayList<>();
    private Long nextId = 1L;

    @GetMapping
    public List<Task> getAllTasks() {
        return tasks;
    }

    @GetMapping("/{id}")
    public Task getTaskById (@PathVariable Long id) {
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);
    }

    @PostMapping
    //@RequestBody transform the JSON coming in the request body in a Java object
    public Task createTask(@RequestBody Task task)  {
        task.setId(nextId++);
        task.setCreatedAt(LocalDateTime.now());
        task.setCompleted(false);
        tasks.add(task);

        return task;
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

    @PutMapping("/{id}")
    public Task updateTask (@PathVariable Long id, @RequestBody Task updatedTask) {
        Task foundTask = tasks.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);

        updatedTask.setId(id);
        updatedTask.setCreatedAt(foundTask.getCreatedAt());

        int index = tasks.indexOf(foundTask);

        tasks.set(index, updatedTask);

        return updatedTask;
    }


}
