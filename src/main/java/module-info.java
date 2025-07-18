module com.yourname.scheduler {
    // Modules our application requires
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // For JDBC
    requires java.net.http; // For the API client
    requires com.google.gson; // For JSON processing

    // Open our packages to JavaFX and Gson so they can access them via reflection
    opens com.yourname.scheduler.controller to javafx.fxml;
    opens com.yourname.scheduler.model to com.google.gson, javafx.base;

    // Export the main package so JavaFX can launch the app
    exports com.yourname.scheduler;
}