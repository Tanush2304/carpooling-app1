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
import javafx.scene.web.WebView;

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
    private TextField sourceField, destinationField, seatsField, priceField;
    @FXML
    private ComboBox<String> hourCombo, minuteCombo;
    @FXML
    private DatePicker datePicker;
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

    @FXML
    private javafx.scene.web.WebView mapWebView;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + UserSession.getUserName());
        }
        setupRideTable();
        setupRequestTable();
        refreshRides();

        setupAutocomplete(sourceField);
        setupAutocomplete(destinationField);
        loadMap();

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                java.time.LocalDate today = java.time.LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        });

        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourCombo.setItems(hours);

        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i += 5) {
            minutes.add(String.format("%02d", i));
        }
        minuteCombo.setItems(minutes);
    }

    private void loadMap() {
        String url = getClass().getResource("/com/nichi/carpoolingapp/map.html").toExternalForm();
        mapWebView.getEngine().load(url);
    }

    private void setupAutocomplete(TextField textField) {
        ContextMenu suggestions = new ContextMenu();

        String[] defaultCities = { "Bangalore", "Davangere", "Delhi", "Hyderabad", "Chennai", "Pune", "Kolkata",
                "Ahmedabad" };

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && textField.getText().isEmpty()) {
                suggestions.getItems().clear();
                for (String city : defaultCities) {
                    MenuItem item = new MenuItem(city);
                    item.setOnAction(e -> {
                        textField.setText(city);
                        updateMapMarker(city, textField == sourceField);
                    });
                    suggestions.getItems().add(item);
                }
                suggestions.show(textField, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.length() < 3) {
                // Keep default suggestions if empty
                if (newValue == null || newValue.isEmpty())
                    return;
                suggestions.hide();
                return;
            }
            new Thread(() -> {
                List<String> results = fetchSuggestions(newValue);
                javafx.application.Platform.runLater(() -> {
                    if (results.isEmpty()) {
                        suggestions.hide();
                    } else {
                        suggestions.getItems().clear();
                        for (String place : results) {
                            MenuItem item = new MenuItem(place);
                            item.setOnAction(e -> {
                                textField.setText(place);
                                suggestions.hide();
                                updateMapMarker(place, textField == sourceField);
                            });
                            suggestions.getItems().add(item);
                        }
                        suggestions.show(textField, javafx.geometry.Side.BOTTOM, 0, 0);
                    }
                });
            }).start();
        });
    }

    private List<String> fetchSuggestions(String query) {
        List<String> list = new java.util.ArrayList<>();
        try {
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&countrycodes=in&q="
                    + java.net.URLEncoder.encode(query, "UTF-8");
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "CarpoolingApp/1.0");

            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader in = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String json = response.toString();
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"display_name\":\"(.*?)\"");
                java.util.regex.Matcher m = p.matcher(json);
                while (m.find()) {
                    list.add(m.group(1));
                    if (list.size() >= 5)
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
    private void publishRide() {
        try {
            String source = sourceField.getText();
            String dest = destinationField.getText();
            Date date = Date.valueOf(datePicker.getValue());

            String selectedHour = hourCombo.getValue();
            String selectedMinute = minuteCombo.getValue();
            if (selectedHour == null || selectedMinute == null) {
                showAlert("Error", "Please select a time.");
                return;
            }
            Time time = Time.valueOf(selectedHour + ":" + selectedMinute + ":00");

            int seats = Integer.parseInt(seatsField.getText());
            double price = Double.parseDouble(priceField.getText());
            int driverId = UserSession.getUserId();

            String idPath = "VERIFIED_USER";
            String carPath = "VERIFIED_USER";

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
        refreshRequests();
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

                EmailService.sendRejectionEmail("tanush.nis21b@gmail.com", UserSession.getUserName(),
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

            String customerEmail = UserDAO.getEmailById(request.getUserId());

            if (customerEmail != null) {
                EmailService.sendBookingConfirmation("tanush.nis21b@gmail.com", UserSession.getUserName(),
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
    public void logout() {
        System.out.println("DriverDashboardController: Logging out...");
        UserSession.clear();
        SceneUtil.load("login.fxml");
    }

    private void updateMapMarker(String location, boolean isSource) {
        if (mapWebView != null) {
            String script = String.format("searchLocation('%s', %b)", location.replace("'", "\\'"), isSource);
            mapWebView.getEngine().executeScript(script);
        }
    }
}
