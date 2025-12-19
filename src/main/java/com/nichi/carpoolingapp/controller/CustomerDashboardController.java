package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.SceneUtil;
import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.RequestDAO;
import com.nichi.carpoolingapp.dao.RideDAO;
import com.nichi.carpoolingapp.model.Request;
import com.nichi.carpoolingapp.model.Ride;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class CustomerDashboardController {

    @FXML
    private TextField searchSource, searchDest;
    @FXML
    private DatePicker searchDate;
    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Ride> searchResultsTable;
    @FXML
    private TableColumn<Ride, Integer> colId;
    @FXML
    private TableColumn<Ride, String> colSource, colDest;
    @FXML
    private TableColumn<Ride, Date> colDate;
    @FXML
    private TableColumn<Ride, Time> colTime;
    @FXML
    private TableColumn<Ride, Double> colPrice;
    @FXML
    private TableColumn<Ride, Button> colAction;

    @FXML
    private TableView<Request> myBookingsTable;
    @FXML
    private TableColumn<Request, String> colBookSource, colBookDest, colBookDriver, colBookStatus;
    @FXML
    private TableColumn<Request, Date> colBookDate;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + UserSession.getUserName());
        }
        setupTable();
        setupBookingsTable();
        refreshBookings();
    }

    @FXML
    private void logout() {
        UserSession.clear();
        SceneUtil.load("login.fxml");
    }

    private void setupTable() {
        if (colId == null)
            return;
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colDest.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("rideDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("rideTime"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colAction.setCellValueFactory(cellData -> {
            Button btn = new Button("Request");
            Ride ride = cellData.getValue();
            btn.setOnAction(e -> requestRide(ride));
            return new SimpleObjectProperty<>(btn);
        });
    }

    private void setupBookingsTable() {
        if (colBookSource == null)
            return;
        colBookSource.setCellValueFactory(new PropertyValueFactory<>("rideSource"));
        colBookDest.setCellValueFactory(new PropertyValueFactory<>("rideDestination"));
        colBookDate.setCellValueFactory(new PropertyValueFactory<>("rideDate"));
        colBookDriver.setCellValueFactory(new PropertyValueFactory<>("driverName"));
        colBookStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    private void refreshBookings() {
        if (myBookingsTable == null)
            return;
        List<Request> bookings = RequestDAO.getRequestsByUserId(UserSession.getUserId());
        myBookingsTable.setItems(FXCollections.observableArrayList(bookings));
    }

    @FXML
    private void searchRides() {
        try {
            String source = searchSource.getText();
            String dest = searchDest.getText();
            Date date = Date.valueOf(searchDate.getValue());

            List<Ride> rides = RideDAO.searchRides(source, dest, date);
            searchResultsTable.setItems(FXCollections.observableArrayList(rides));

            if (rides.isEmpty()) {
                showAlert("Info", "No rides found.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid search criteria.");
        }
    }

    private void requestRide(Ride ride) {
        Request req = new Request(ride.getId(), UserSession.getUserId(), "PENDING");
        if (RequestDAO.createRequest(req)) {
            showAlert("Success", "Ride requested successfully!");
        } else {
            showAlert("Error", "Failed to request ride.");
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
