package com.yourname.scheduler.taskapi.controller;

import com.yourname.scheduler.taskapi.model.Task;
import com.yourname.scheduler.taskapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") // Base URL for all endpoints in this controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // GET /api/tasks -> Get all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // POST /api/tasks -> Create a new task
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    // PUT /api/tasks/{id} -> Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setDueDate(taskDetails.getDueDate());
                    task.setPriority(taskDetails.getPriority());
                    task.setStatus(taskDetails.getStatus());
                    task.setEstimatedDurationMinutes(taskDetails.getEstimatedDurationMinutes());
                    task.setCompletionDate(taskDetails.getCompletionDate());
                    Task updatedTask = taskRepository.save(task);
                    return ResponseEntity.ok(updatedTask);
                }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id} -> Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}