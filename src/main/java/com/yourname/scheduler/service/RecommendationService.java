package com.yourname.scheduler.service;

import com.yourname.scheduler.model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecommendationService {

    // Define time slots
    private enum TimeOfDay { MORNING, AFTERNOON, EVENING }

    /**
     * Analyzes all tasks and provides the most relevant suggestion.
     * This is the main entry point for getting recommendations.
     */
    public String getSmartSuggestion(List<Task> allTasks) {
        // Find the next task to suggest first
        Optional<Task> nextTask = findTaskByPriority(allTasks, Task.Priority.HIGH)
                .or(() -> findTaskByPriority(allTasks, Task.Priority.MEDIUM))
                .or(() -> findTaskByPriority(allTasks, Task.Priority.LOW));

        // Rule 1 (New Priority): Habit check runs first for the suggested task.
        if (nextTask.isPresent()) {
            Optional<String> habitSuggestion = suggestTimeBasedOnHabits(nextTask.get(), allTasks);
            if (habitSuggestion.isPresent()) {
                return habitSuggestion.get(); // If we find a habit, show it and exit.
            }
        }

        // Rule 2: Procrastination check runs only if no habit was found.
        Optional<String> procrastinationWarning = checkForProcrastination(allTasks);
        if (procrastinationWarning.isPresent()) {
            return procrastinationWarning.get();
        }

        // Rule 3 (Fallback): General suggestion for the most important task.
        if (nextTask.isPresent()) {
            return "💡 Suggestion: Focus on '" + nextTask.get().getTitle() + "' next.";
        }

        // Default message if no tasks are left.
        return "Everything looks good! Add a new task to get started.";
    }

    /**
     * NEW RULE: Analyzes past completed tasks to find the user's preferred time slot
     * for tasks with similar titles.
     * @param taskToSuggest The task we are making a suggestion for.
     * @param allTasks The full list of tasks to find historical data.
     * @return A suggestion string if a pattern is found.
     */
    private Optional<String> suggestTimeBasedOnHabits(Task taskToSuggest, List<Task> allTasks) {
        // Step 1: Extract the primary keyword from the task title (e.g., "Study", "Workout").
        String keyword = getTitleKeyword(taskToSuggest.getTitle());
        if (keyword == null) {
            return Optional.empty(); // Cannot find a keyword to match.
        }

        // Step 2: Filter for completed tasks that share the same keyword.
        List<Task> similarCompletedTasks = allTasks.stream()
                .filter(task -> task.getStatus() == Task.Status.DONE &&
                        task.getCompletionDate() != null &&
                        getTitleKeyword(task.getTitle()).equalsIgnoreCase(keyword))
                .collect(Collectors.toList());

        // Step 3: We need enough data to make a confident recommendation.
        if (similarCompletedTasks.size() < 3) {
            return Optional.empty(); // Not enough historical data.
        }

        // Step 4: Tally the completions by time of day.
        Map<TimeOfDay, Long> completionsByTimeOfDay = similarCompletedTasks.stream()
                .collect(Collectors.groupingBy(task -> getTimeOfDay(task.getCompletionDate()), Collectors.counting()));

        // Step 5: Find the most frequent time slot.
        Optional<Map.Entry<TimeOfDay, Long>> mostFrequentSlot = completionsByTimeOfDay.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentSlot.isPresent()) {
            String timeSuggestion = mostFrequentSlot.get().getKey().toString().toLowerCase();
            return Optional.of(String.format("🚀 Habit Alert: You often complete '%s' tasks in the %s. Try tackling '%s' then!",
                    keyword, timeSuggestion, taskToSuggest.getTitle()));
        }

        return Optional.empty();
    }

    // --- Helper Methods ---

    private Optional<String> checkForProcrastination(List<Task> allTasks) {
        // ... (this method is unchanged)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        for (Task task : allTasks) {
            if (task.getPriority() == Task.Priority.HIGH &&
                    task.getStatus() == Task.Status.TODO &&
                    (task.getDueDate().isEqual(LocalDate.now()) || task.getDueDate().isEqual(tomorrow))) {
                return Optional.of("⚠️ WARNING: High-priority task '" + task.getTitle() + "' is due soon and hasn't been started!");
            }
        }
        return Optional.empty();
    }

    private Optional<Task> findTaskByPriority(List<Task> tasks, Task.Priority priority) {
        // ... (this method is unchanged)
        return tasks.stream()
                .filter(task -> task.getStatus() != Task.Status.DONE && task.getPriority() == priority)
                .findFirst();
    }

    /**
     * A simple helper to extract the first word from a title as a keyword.
     * e.g., "Study for Math Exam" -> "Study"
     */
    private String getTitleKeyword(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "";
        }
        // A simple list of common words to ignore.
        List<String> stopWords = List.of("a", "an", "the", "for", "to", "in", "of", "on");

        // Find the first word that is not a stop word.
        for (String word : title.toLowerCase().split(" ")) {
            if (!stopWords.contains(word)) {
                // Return it capitalized for display.
                return word.substring(0, 1).toUpperCase() + word.substring(1);
            }
        }
        // Fallback to the first word if all are stop words (unlikely).
        return title.split(" ")[0];
    }

    /**
     * A helper to classify a LocalDateTime into a general time of day.
     */
    private TimeOfDay getTimeOfDay(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        if (hour >= 5 && hour < 12) {
            return TimeOfDay.MORNING;
        } else if (hour >= 12 && hour < 18) {
            return TimeOfDay.AFTERNOON;
        } else {
            return TimeOfDay.EVENING;
        }
    }
}