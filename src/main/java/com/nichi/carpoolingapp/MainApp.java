package com.nichi.carpoolingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override

    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader =
                new FXMLLoader(MainApp.class.getResource("login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setMaximized(true);
        stage.setMinWidth(900);
        stage.setMinHeight(700);

        scene.getStylesheets().add(
                MainApp.class.getResource("app.css").toExternalForm()
        );

        stage.setTitle("Carpooling App");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
