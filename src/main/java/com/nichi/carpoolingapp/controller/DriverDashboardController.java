package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.SceneUtil;
import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.RequestDAO;
import com.nichi.carpoolingapp.dao.RideDAO;
import com.nichi.carpoolingapp.dao.UserDAO;
import com.nichi.carpoolingapp.model.Request;
import com.nichi.carpoolingapp.model.Ride;
import com.nichi.carpoolingapp.service.EmailService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class DriverDashboardController {

    @FXML
    private TextField sourceField, destinationField, timeField, seatsField, priceField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label idProofLabel, carPhotoLabel;
    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Ride> myRidesTable;
    @FXML
    private TableColumn<Ride, Integer> colId;
    @FXML
    private TableColumn<Ride, String> colSource, colDest, colStatus;
    @FXML
    private TableColumn<Ride, Date> colDate;
    @FXML
    private TableColumn<Ride, Time> colTime;
    @FXML
    private TableColumn<Ride, Integer> colSeats;
    @FXML
    private TableColumn<Ride, Double> colPrice;

    @FXML
    private TableView<Request> requestsTable;
    @FXML
    private TableColumn<Request, Integer> colReqById, colReqRideId;
    @FXML
    private TableColumn<Request, String> colReqStatus, colReqCustomerName;
    @FXML
    private TableColumn<Request, Button> colReqActions;

    @FXML
    private TableColumn<Ride, Button> colRideAction; // New column for Complete button

    private File selectedIdProof;
    private File selectedCarPhoto;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + UserSession.getUserName());
        }
        setupRideTable();
        setupRequestTable();
        refreshRides();
    }

    private void setupRideTable() {
        if (colId == null)
            return;
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colDest.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("rideDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("rideTime"));
        colSeats.setCellValueFactory(new PropertyValueFactory<>("seats"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colRideAction.setCellValueFactory(cellData -> {
            Button btn = new Button("Complete");
            Ride ride = cellData.getValue();
            if (!"OPEN".equals(ride.getStatus())) {
                btn.setDisable(true);
                btn.setText(ride.getStatus());
            } else {
                btn.setOnAction(e -> completeRide(ride));
            }
            return new SimpleObjectProperty<>(btn);
        });
    }

    private void setupRequestTable() {
        if (colReqById == null)
            return;
        colReqById.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReqRideId.setCellValueFactory(new PropertyValueFactory<>("rideId"));
        // Display Customer Name instead of User ID
        colReqCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colReqStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colReqActions.setCellFactory(param -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accept");
            private final Button rejectBtn = new Button("Reject");
            private final HBox pane = new HBox(5, acceptBtn, rejectBtn);

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Request req = getTableView().getItems().get(getIndex());

                if (!"PENDING".equals(req.getStatus())) {
                    setText(req.getStatus());
                    setGraphic(null);
                } else {
                    setText(null);
                    acceptBtn.setOnAction(e -> acceptRequest(req));
                    rejectBtn.setOnAction(e -> rejectRequest(req));
                    rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // Red for reject
                    setGraphic(pane);
                }
            }
        });
    }

    @FXML
    private void uploadIdProof() {
        selectedIdProof = chooseFile("Select ID Proof");
        if (selectedIdProof != null)
            idProofLabel.setText(selectedIdProof.getName());
    }

    @FXML
    private void uploadCarPhoto() {
        selectedCarPhoto = chooseFile("Select Car Photo");
        if (selectedCarPhoto != null)
            carPhotoLabel.setText(selectedCarPhoto.getName());
    }

    private File chooseFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return chooser.showOpenDialog(new Stage());
    }

    @FXML
    private void publishRide() {
        try {
            String source = sourceField.getText();
            String dest = destinationField.getText();
            Date date = Date.valueOf(datePicker.getValue());
            Time time = Time.valueOf(timeField.getText() + ":00"); // Basic parsing
            int seats = Integer.parseInt(seatsField.getText());
            double price = Double.parseDouble(priceField.getText());
            int driverId = UserSession.getUserId();

            if (selectedIdProof == null || selectedCarPhoto == null) {
                showAlert("Error", "Please upload ID Proof and Car Photo.");
                return;
            }

            // Save files
            String uploadDir = "uploads/";
            new File(uploadDir).mkdirs();
            String idPath = uploadDir + System.currentTimeMillis() + "_id_" + selectedIdProof.getName();
            String carPath = uploadDir + System.currentTimeMillis() + "_car_" + selectedCarPhoto.getName();

            Files.copy(selectedIdProof.toPath(), Paths.get(idPath), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(selectedCarPhoto.toPath(), Paths.get(carPath), StandardCopyOption.REPLACE_EXISTING);

            Ride ride = new Ride(driverId, source, dest, date, time, seats, price, "OPEN", idPath, carPath);
            if (RideDAO.publishRide(ride)) {
                showAlert("Success", "Ride published successfully!");
                refreshRides();
            } else {
                showAlert("Error", "Failed to publish ride.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void refreshRides() {
        if (myRidesTable == null)
            return;
        List<Ride> rides = RideDAO.getRidesByDriverId(UserSession.getUserId());
        myRidesTable.setItems(FXCollections.observableArrayList(rides));
        refreshRequests(); // Also refresh requests for these rides
    }

    @FXML
    private void refreshRequests() {
        if (requestsTable == null)
            return;
        List<Ride> myRides = RideDAO.getRidesByDriverId(UserSession.getUserId());
        ObservableList<Request> allRequests = FXCollections.observableArrayList();
        for (Ride r : myRides) {
            allRequests.addAll(RequestDAO.getRequestsByRideId(r.getId()));
        }
        requestsTable.setItems(allRequests);
    }

    private void completeRide(Ride ride) {
        if (RideDAO.updateRideStatus(ride.getId(), "DONE")) {
            showAlert("Success", "Ride marked as DONE!");
            refreshRides();
        } else {
            showAlert("Error", "Failed to update ride status.");
        }
    }

    private void rejectRequest(Request request) {
        if (RequestDAO.updateRequestStatus(request.getId(), "REJECTED")) {
            showAlert("Success", "Request rejected.");

            // Send Rejection Email
            String customerEmail = UserDAO.getEmailById(request.getUserId());
            if (customerEmail != null) {
                // Fetch ride details to include in email
                // For simplicity, we might just pass ID, or fetch ride object.
                // Ideally we cache or fetch ride info.
                EmailService.sendRejectionEmail(customerEmail, UserSession.getUserName(),
                        "Ride ID: " + request.getRideId());
            }
            refreshRequests();
        } else {
            showAlert("Error", "Could not reject request.");
        }
    }

    private void acceptRequest(Request request) {
        if (RequestDAO.updateRequestStatus(request.getId(), "ACCEPTED")) {
            showAlert("Success", "Request accepted!");

            // Get Customer Email details using the new DAO method
            String customerEmail = UserDAO.getEmailById(request.getUserId());

            if (customerEmail != null) {
                EmailService.sendBookingConfirmation(customerEmail, UserSession.getUserName(),
                        UserSession.getUserEmail());
            } else {
                System.out.println("Could not find email for user ID: " + request.getUserId());
            }

            refreshRequests();
        } else {
            showAlert("Error", "Could not accept request.");
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

    @FXML
    private void logout() {
        UserSession.clear();
        SceneUtil.load("login.fxml");
    }
}
