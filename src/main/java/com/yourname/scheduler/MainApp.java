package com.yourname.scheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // The path must start with a "/" to be an absolute path from the classpath root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Intelligent Task Scheduler");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}