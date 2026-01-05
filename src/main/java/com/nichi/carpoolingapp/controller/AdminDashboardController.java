package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.SceneUtil;
import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.UserDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private TableView<UserDAO.PendingDriver> pendingTable;
    @FXML
    private TableColumn<UserDAO.PendingDriver, Integer> colId;
    @FXML
    private TableColumn<UserDAO.PendingDriver, String> colName, colEmail, colLicense, colCar;
    @FXML
    private TableColumn<UserDAO.PendingDriver, Button> colAction;

    @FXML
    public void initialize() {
        setupTable();
        refreshList();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLicense.setCellValueFactory(new PropertyValueFactory<>("licensePath"));
        colCar.setCellValueFactory(new PropertyValueFactory<>("carPath"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox pane = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.getStyleClass().add("button-action");
                rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> handleAction(getTableView().getItems().get(getIndex()), "APPROVED"));
                rejectBtn.setOnAction(e -> handleAction(getTableView().getItems().get(getIndex()), "REJECTED"));
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void handleAction(UserDAO.PendingDriver driver, String status) {
        if (UserDAO.updateDriverStatus(driver.id, status)) {
            showAlert("Success", "Driver " + status);
            refreshList();
        } else {
            showAlert("Error", "Failed to update status.");
        }
    }

    @FXML
    private void refreshList() {
        List<UserDAO.PendingDriver> pending = UserDAO.getPendingDrivers();
        pendingTable.setItems(FXCollections.observableArrayList(pending));
    }

    @FXML
    public void logout() {
        System.out.println("AdminDashboardController: Logging out...");
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
