package com.nichi.carpoolingapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneUtil {

    public static void load(String fxml) {
        System.out.println("SceneUtil loading: " + fxml);
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(w -> w.isShowing()).get(0);

            FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxml));

            Scene scene = new Scene(
                    loader.load(),
                    stage.getWidth(),
                    stage.getHeight());

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
