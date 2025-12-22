package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.SceneUtil;
import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DriverVerificationController {

    @FXML
    private Label licenseLabel;
    @FXML
    private Label carLabel;

    private File selectedLicense;
    private File selectedCarPhoto;

    @FXML
    private void uploadLicense() {
        File file = chooseFile("Select Driving License");
        if (file != null) {
            selectedLicense = file;
            licenseLabel.setText(file.getName());
        }
    }

    @FXML
    private void uploadCarPhoto() {
        File file = chooseFile("Select Car Photo");
        if (file != null) {
            selectedCarPhoto = file;
            carLabel.setText(file.getName());
        }
    }

    private File chooseFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return fileChooser.showOpenDialog(new Stage());
    }

    @FXML
    private void submitVerification() {
        if (selectedLicense == null || selectedCarPhoto == null) {
            showAlert("Error", "Please upload both documents.");
            return;
        }

        String licensePath = selectedLicense.getAbsolutePath();
        String carPath = selectedCarPhoto.getAbsolutePath();

        if (UserDAO.updateDriverDocuments(UserSession.getUserId(), licensePath, carPath)) {

            com.nichi.carpoolingapp.service.EmailService.sendDriverVerificationAlert(
                    "tanush.nis21b@gmail.com",
                    UserSession.getUserName(),
                    licensePath,
                    carPath);

            showAlert("Success", "Documents submitted! Please wait for Admin approval.");
            SceneUtil.load("role-choice.fxml");
        } else {
            showAlert("Error", "Failed to submit documents.");
        }
    }

    @FXML
    private void goBack() {
        SceneUtil.load("role-choice.fxml");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
