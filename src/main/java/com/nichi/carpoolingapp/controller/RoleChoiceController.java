package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import com.nichi.carpoolingapp.SceneUtil;

public class RoleChoiceController {
    @FXML
    private Label userNameLabel;
    @FXML
    private javafx.scene.control.Button adminDashboardBtn;

    @FXML
    public void initialize() {
        String name = UserSession.getUserName();
        userNameLabel.setText(name != null ? name : "User");

        // Admin Visibility Check
        String email = UserDAO.getEmailById(UserSession.getUserId());
        if ("tanush.nis21b@gmail.com".equals(email)) {
            if (adminDashboardBtn != null)
                adminDashboardBtn.setVisible(true);
        } else {
            if (adminDashboardBtn != null)
                adminDashboardBtn.setVisible(false);
        }
    }

    @FXML
    private void openDriverDashboard() {
        int userId = UserSession.getUserId();
        String status = UserDAO.getDriverStatus(userId);

        switch (status) {
            case "APPROVED":
                SceneUtil.load("driver-dashboard.fxml");
                break;
            case "PENDING":
                showAlert("Verification Pending",
                        "Your documents are under review by the Admin. Please wait for approval.");
                break;
            case "NONE":
            case "REJECTED":
                SceneUtil.load("driver-verification.fxml");
                break;
            default:
                SceneUtil.load("driver-verification.fxml");
        }
    }

    @FXML
    private void openCustomerDashboard() {
        SceneUtil.load("customer-dashboard.fxml");
    }

    @FXML
    private void adminLogin() {
        String email = UserDAO.getEmailById(UserSession.getUserId());
        if ("tanush.nis21b@gmail.com".equals(email)) {
            SceneUtil.load("admin-dashboard.fxml");
        } else {
            showAlert("Access Denied", "You do not have Admin privileges.");
        }
    }

    @FXML
    public void logout() {
        System.out.println(">>> RoleChoiceController: Logout button CLICKED");
        UserSession.clear();
        SceneUtil.load("login.fxml");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
