package com.yourname.scheduler.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Status { TODO, IN_PROGRESS, DONE }

    // --- ID is now Integer, not int ---
    private Integer id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Status status;
    private int estimatedDurationMinutes;
    private LocalDateTime completionDate;

    // Constructor for creating a NEW task from the UI
    public Task(String title, String description, LocalDate dueDate, Priority priority, int estimatedDurationMinutes) {
        this.id = null; // A new task has a null ID
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = Status.TODO;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.completionDate = null;
    }

    // Constructor for loading an EXISTING task from the API (or DB)
    public Task(Integer id, String title, String description, LocalDate dueDate, Priority priority, Status status, int estimatedDurationMinutes, LocalDateTime completionDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.completionDate = completionDate;
    }

    // --- Corrected Getters and Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    // ... other getters and setters remain the same ...
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }
    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    @Override
    public String toString() {
        return title;
    }
}