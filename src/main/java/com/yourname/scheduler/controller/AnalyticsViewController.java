package com.yourname.scheduler.controller;

import com.yourname.scheduler.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsViewController {

    @FXML private PieChart statusPieChart;
    @FXML private PieChart priorityPieChart;
    @FXML private Button closeButton;

    // private TaskDAO taskDAO; // <-- REMOVED

    @FXML
    public void initialize() {
        // Data loading is now handled by initData(), not here.
    }

    /**
     * This method is called by the main controller to pass in the task data.
     * @param allTasks The list of all tasks from the main view.
     */
    public void initData(List<Task> allTasks) {
        loadStatusChartData(allTasks);
        loadPriorityChartData(allTasks);
    }

    // This method now calculates from a list, not a DAO.
    private void loadStatusChartData(List<Task> allTasks) {
        Map<Task.Status, Long> statusData = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        statusData.forEach((status, count) -> pieChartData.add(new PieChart.Data(status.name(), count)));
        statusPieChart.setData(pieChartData);
    }

    // This method now calculates from a list, not a DAO.
    private void loadPriorityChartData(List<Task> allTasks) {
        Map<Task.Priority, Long> priorityData = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        priorityData.forEach((priority, count) -> pieChartData.add(new PieChart.Data(priority.name(), count)));
        priorityPieChart.setData(pieChartData);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}