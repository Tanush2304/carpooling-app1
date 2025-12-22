package com.nichi.carpoolingapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneUtil {

    public static void load(String fxml) {
        System.out.println(">>> SceneUtil: Attempting to load FXML: " + fxml);
        try {
            Stage stage = (Stage) javafx.stage.Window.getWindows().stream()
                    .filter(w -> w instanceof Stage && w.isShowing())
                    .findFirst()
                    .orElse(null);

            if (stage == null) {
                throw new RuntimeException("No active stage found to load: " + fxml);
            }

            FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxml));

            Scene scene = new Scene(
                    loader.load(),
                    stage.getWidth(),
                    stage.getHeight());

            // Apply global CSS
            scene.getStylesheets().add(
                    SceneUtil.class.getResource("app.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load FXML");
            alert.setContentText("File: " + fxml + "\nError: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
