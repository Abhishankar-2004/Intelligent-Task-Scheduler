package com.yourname.scheduler.controller;

import com.yourname.scheduler.dao.TaskApiClient;
import com.yourname.scheduler.model.Task;
import com.yourname.scheduler.service.RecommendationService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TaskViewController {

    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, Task.Priority> priorityColumn;
    @FXML private TableColumn<Task, LocalDate> dueDateColumn;
    @FXML private TableColumn<Task, Task.Status> statusColumn;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private ChoiceBox<Task.Priority> priorityChoiceBox;
    @FXML private TextField durationField;
    @FXML private Label recommendationLabel;

    private TaskApiClient taskApiClient;
    private ObservableList<Task> taskList;
    private RecommendationService recommendationService;

    @FXML
    public void initialize() {
        taskApiClient = new TaskApiClient();
        recommendationService = new RecommendationService();

        // **FIXED THE TYPO HERE**
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        priorityChoiceBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        priorityChoiceBox.setValue(Task.Priority.MEDIUM);

        loadTaskData();

        taskTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showTaskDetails(newVal));
    }

    private void loadTaskData() {
        try {
            List<Task> tasksFromServer = taskApiClient.getAllTasks();
            taskList = FXCollections.observableArrayList(tasksFromServer);
            taskTableView.setItems(taskList);
        } catch (Exception e) {
            showApiErrorAlert(e);
            taskList = FXCollections.observableArrayList();
            taskTableView.setItems(taskList);
        }
        updateRecommendation();
    }

    @FXML
    private void handleAddTask() {
        if (!isInputValid()) return;
        try {
            Task newTask = new Task(
                    titleField.getText(),
                    descriptionArea.getText(),
                    dueDatePicker.getValue(),
                    priorityChoiceBox.getValue(),
                    Integer.parseInt(durationField.getText())
            );
            taskApiClient.addTask(newTask);
            loadTaskData();
            clearFields();
        } catch (Exception e) {
            showApiErrorAlert(e);
        }
    }

    @FXML
    private void handleUpdateTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("No Task Selected", "Please select a task to update.");
            return;
        }
        if (!isInputValid()) return;

        try {
            selectedTask.setTitle(titleField.getText());
            selectedTask.setDescription(descriptionArea.getText());
            selectedTask.setDueDate(dueDatePicker.getValue());
            selectedTask.setPriority(priorityChoiceBox.getValue());
            selectedTask.setEstimatedDurationMinutes(Integer.parseInt(durationField.getText()));
            taskApiClient.updateTask(selectedTask);
            loadTaskData();
        } catch (Exception e) {
            showApiErrorAlert(e);
        }
    }

    @FXML
    private void handleMarkAsDone() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("No Task Selected", "Please select a task to mark as done.");
            return;
        }
        try {
            selectedTask.setStatus(Task.Status.DONE);
            selectedTask.setCompletionDate(LocalDateTime.now());
            taskApiClient.updateTask(selectedTask);
            loadTaskData();
        } catch (Exception e) {
            showApiErrorAlert(e);
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("No Task Selected", "Please select a task to delete.");
            return;
        }
        try {
            taskApiClient.deleteTask(selectedTask.getId());
            loadTaskData();
        } catch (Exception e) {
            showApiErrorAlert(e);
        }
    }

    @FXML
    private void handleShowAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AnalyticsView.fxml"));
            Parent root = loader.load();
            AnalyticsViewController controller = loader.getController();
            controller.initData(taskList);
            Stage analyticsStage = new Stage();
            analyticsStage.setTitle("Productivity Analytics");
            analyticsStage.setScene(new Scene(root));
            analyticsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open analytics window.");
        }
    }

    private void showTaskDetails(Task task) {
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            dueDatePicker.setValue(task.getDueDate());
            priorityChoiceBox.setValue(task.getPriority());
            durationField.setText(String.valueOf(task.getEstimatedDurationMinutes()));
        } else {
            clearFields();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (titleField.getText() == null || titleField.getText().isEmpty()) errorMessage += "No valid title!\n";
        if (dueDatePicker.getValue() == null) errorMessage += "No valid due date!\n";
        if (priorityChoiceBox.getValue() == null) errorMessage += "No valid priority!\n";
        if (durationField.getText() == null || durationField.getText().isEmpty()) {
            errorMessage += "No valid duration!\n";
        } else {
            try { Integer.parseInt(durationField.getText()); } catch (NumberFormatException e) {
                errorMessage += "Duration must be a number!\n";
            }
        }
        if (errorMessage.isEmpty()) return true;
        else { showAlert("Invalid Fields", errorMessage); return false; }
    }

    private void clearFields() {
        titleField.clear();
        descriptionArea.clear();
        dueDatePicker.setValue(null);
        priorityChoiceBox.setValue(Task.Priority.MEDIUM);
        durationField.clear();
        taskTableView.getSelectionModel().clearSelection();
    }

    private void updateRecommendation() {
        if (taskList.isEmpty()) {
            recommendationLabel.setText("Add a new task to get your first recommendation!");
            return;
        }
        String suggestion = recommendationService.getSmartSuggestion(taskList);
        recommendationLabel.setText(suggestion);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showApiErrorAlert(Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("API Connection Error");
            alert.setHeaderText("Could not connect to the server.");
            alert.setContentText("Please ensure the backend server is running and accessible.\n\nError: " + e.getMessage());
            alert.showAndWait();
        });
    }
}