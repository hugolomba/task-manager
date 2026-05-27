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

    @PostMapping
    //@RequestBody transform the JSON coming in the request body in a Java object
    public Task createTask(@RequestBody Task task)  {
        task.setId(nextId++);
        task.setCreatedAt(LocalDateTime.now());
        task.setCompleted(false);
        tasks.add(task);

        return task;
    }


}
