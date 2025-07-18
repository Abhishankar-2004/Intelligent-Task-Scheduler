package com.yourname.scheduler.taskapi.model;

import jakarta.persistence.*; // Note: Uses jakarta.persistence
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Task {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Status { TODO, IN_PROGRESS, DONE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int estimatedDurationMinutes;
    private LocalDateTime completionDate;

    // Getters and Setters for all fields...
    // In IntelliJ: Right-click -> Generate -> Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}