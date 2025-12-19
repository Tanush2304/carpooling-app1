package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.UserSession;
import javafx.fxml.FXML;
import com.nichi.carpoolingapp.SceneUtil;
import javafx.scene.control.Label;

public class RoleChoiceController {
    @FXML
    private Label userNameLabel;

    @FXML
    public void initialize() {
        String name = UserSession.getUserName();
        userNameLabel.setText(name != null ? name : "User");
    }

    @FXML
    private void openDriverDashboard() {
        SceneUtil.load("driver-dashboard.fxml");
    }

    @FXML
    private void openCustomerDashboard() {
        SceneUtil.load("customer-dashboard.fxml");
    }

    @FXML
    private void logout() {
        System.out.println("LOGOUT CLICKED");
        UserSession.clear();
        SceneUtil.load("login.fxml");
    }
}
