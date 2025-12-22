package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.SceneUtil;
import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private Label loginTab;
    @FXML
    private Label registerTab;

    @FXML
    private VBox loginForm;
    @FXML
    private VBox registerForm;

    @FXML
    private TextField loginEmail;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField loginPasswordVisible;

    @FXML
    private TextField regName;
    @FXML
    private TextField regEmail;
    @FXML
    private PasswordField regPassword;
    @FXML
    private TextField regPasswordVisible;

    @FXML
    private void showLogin() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);

        registerForm.setVisible(false);
        registerForm.setManaged(false);

        loginTab.getStyleClass().setAll("tab-active");
        registerTab.getStyleClass().setAll("tab-inactive");
    }

    @FXML
    private void showRegister() {
        registerForm.setVisible(true);
        registerForm.setManaged(true);

        loginForm.setVisible(false);
        loginForm.setManaged(false);

        registerTab.getStyleClass().setAll("tab-active");
        loginTab.getStyleClass().setAll("tab-inactive");
    }

    @FXML
    private void handleLogin() {

        String email = loginEmail.getText();
        String password = loginPassword.isVisible()
                ? loginPassword.getText()
                : loginPasswordVisible.getText();

        if (!validateLogin(email, password)) {
            return;
        }

        if (!UserDAO.userExists(email)) {
            alert("Account not found. Please sign up first.");
            showRegister();
            return;
        }

        boolean valid = UserDAO.loginUser(email, password);

        if (valid) {
            // alert("Login successful");
            int userId = UserDAO.getUserIdByEmail(email);
            UserSession.setUser(
                    userId,
                    UserDAO.getUserNameByEmail(email),
                    email);

            if ("tanush.nis21b@gmail.com".equals(email)) {
                SceneUtil.load("admin-dashboard.fxml");
            } else {
                SceneUtil.load("role-choice.fxml");
            }

            clearLoginFields();

        } else {
            alert("Incorrect password");
        }
    }

    @FXML
    private void handleRegister() {

        String name = regName.getText();
        String email = regEmail.getText();
        String password = regPassword.isVisible()
                ? regPassword.getText()
                : regPasswordVisible.getText();

        if (!validateSignup(name, email, password)) {
            return;
        }

        if (UserDAO.emailExists(email)) {
            alert("Email already registered. Please login.");
            showLogin();
            return;
        }

        boolean success = UserDAO.registerUser(name, email, password);

        if (success) {
            alert("Registration successful. Please login.");
            clearRegisterFields();
            showLogin();
        } else {
            alert("Registration failed");
        }
    }

    @FXML
    private void toggleLoginPassword() {
        if (loginPassword.isVisible()) {
            loginPasswordVisible.setText(loginPassword.getText());
            loginPassword.setVisible(false);
            loginPassword.setManaged(false);

            loginPasswordVisible.setVisible(true);
            loginPasswordVisible.setManaged(true);
        } else {
            loginPassword.setText(loginPasswordVisible.getText());
            loginPasswordVisible.setVisible(false);
            loginPasswordVisible.setManaged(false);

            loginPassword.setVisible(true);
            loginPassword.setManaged(true);
        }
    }

    @FXML
    private void toggleRegisterPassword() {
        if (regPassword.isVisible()) {
            regPasswordVisible.setText(regPassword.getText());
            regPassword.setVisible(false);
            regPassword.setManaged(false);

            regPasswordVisible.setVisible(true);
            regPasswordVisible.setManaged(true);
        } else {
            regPassword.setText(regPasswordVisible.getText());
            regPasswordVisible.setVisible(false);
            regPasswordVisible.setManaged(false);

            regPassword.setVisible(true);
            regPassword.setManaged(true);
        }
    }

    private boolean validateLogin(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            alert("Email is required");
            return false;
        }

        if (!email.contains("@")) {
            alert("Enter a valid email");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            alert("Password is required");
            return false;
        }

        return true;
    }

    private boolean validateSignup(String name, String email, String password) {

        if (name == null || name.trim().isEmpty()) {
            alert("Name is required");
            return false;
        }

        if (email == null || !email.contains("@")) {
            alert("Enter a valid email");
            return false;
        }

        if (password == null || password.length() < 6) {
            alert("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }

    private void clearLoginFields() {
        loginEmail.clear();
        loginPassword.clear();
        loginPasswordVisible.clear();
    }

    private void clearRegisterFields() {
        regName.clear();
        regEmail.clear();
        regPassword.clear();
        regPasswordVisible.clear();
    }
}
