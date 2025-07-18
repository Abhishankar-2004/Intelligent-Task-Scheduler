package com.yourname.scheduler.dao;

import com.yourname.scheduler.model.Task;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDAO {
    private final String url = "jdbc:sqlite:scheduler.db";

    public TaskDAO() {
        // UPDATED TABLE SCHEMA with AI fields
        String sql = "CREATE TABLE IF NOT EXISTS tasks ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " title TEXT NOT NULL,"
                + " description TEXT,"
                + " dueDate TEXT NOT NULL,"
                + " priority TEXT NOT NULL,"
                + " status TEXT NOT NULL,"
                + " estimatedDurationMinutes INTEGER,"
                + " completionDate TEXT"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // UPDATED: addTask includes estimatedDurationMinutes
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks(title, description, dueDate, priority, status, estimatedDurationMinutes, completionDate) "
                + "VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDueDate().toString());
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setInt(6, task.getEstimatedDurationMinutes());
            pstmt.setString(7, task.getCompletionDate() == null ? null : task.getCompletionDate().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // UPDATED: getAllTasks reads all fields including AI ones
    public List<Task> getAllTasks() {
        String sql = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String completionDateStr = rs.getString("completionDate");

                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        LocalDate.parse(rs.getString("dueDate")),
                        Task.Priority.valueOf(rs.getString("priority")),
                        Task.Status.valueOf(rs.getString("status")),
                        rs.getInt("estimatedDurationMinutes"),
                        (completionDateStr == null) ? null : LocalDateTime.parse(completionDateStr)
                );

                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tasks;
    }

    // UPDATED: updateTask includes new fields
    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, dueDate = ?, priority = ?, status = ?, "
                + "estimatedDurationMinutes = ?, completionDate = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDueDate().toString());
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setInt(6, task.getEstimatedDurationMinutes());
            pstmt.setString(7, task.getCompletionDate() == null ? null : task.getCompletionDate().toString());
            pstmt.setInt(8, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Unchanged
    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // ... (keep all the existing code)

    /**
     * Gets the count of tasks for each status (TODO, IN_PROGRESS, DONE).
     * @return A map where the key is the status and the value is the count.
     */
    public Map<String, Integer> getTaskCountByStatus() {
        String sql = "SELECT status, COUNT(*) as count FROM tasks GROUP BY status";
        Map<String, Integer> statusCounts = new HashMap<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                statusCounts.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return statusCounts;
    }

    /**
     * Gets the count of tasks for each priority (HIGH, MEDIUM, LOW).
     * @return A map where the key is the priority and the value is the count.
     */
    public Map<String, Integer> getTaskCountByPriority() {
        String sql = "SELECT priority, COUNT(*) as count FROM tasks GROUP BY priority";
        Map<String, Integer> priorityCounts = new HashMap<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                priorityCounts.put(rs.getString("priority"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return priorityCounts;
    }
}
