package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.SceneUtil;
import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.dao.RequestDAO;
import com.nichi.carpoolingapp.dao.RideDAO;
import com.nichi.carpoolingapp.model.Request;
import com.nichi.carpoolingapp.model.Ride;
import com.nichi.carpoolingapp.service.EmailService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

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
    private TableColumn<Request, Button> colBookPayment;

    @FXML
    private TableColumn<Request, Button> colBookCancel;

    @FXML
    private javafx.scene.web.WebView mapWebView;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + UserSession.getUserName());
        }
        setupTable();
        setupBookingsTable();
        refreshBookings();

        setupAutocomplete(searchSource);
        setupAutocomplete(searchDest);
        loadMap();
    }

    private void loadMap() {
        String url = getClass().getResource("/com/nichi/carpoolingapp/map.html").toExternalForm();
        mapWebView.getEngine().load(url);
    }

    private void setupAutocomplete(TextField textField) {
        ContextMenu suggestions = new ContextMenu();

        String[] default_cities_raw = com.nichi.carpoolingapp.util.ConfigManager
                .get("ui.default_cities", "Bangalore,Delhi,Mumbai").split(",");
        String[] defaultCities = java.util.Arrays.stream(default_cities_raw).map(String::trim).toArray(String[]::new);

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && textField.getText().isEmpty()) {
                suggestions.getItems().clear();
                for (String city : defaultCities) {
                    MenuItem item = new MenuItem(city);
                    item.setOnAction(e -> {
                        textField.setText(city);
                        updateMapMarker(city, textField == searchSource);
                    });
                    suggestions.getItems().add(item);
                }
                suggestions.show(textField, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.length() < 3) {

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
                                updateMapMarker(place, textField == searchSource);
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
            String baseUrl = com.nichi.carpoolingapp.util.ConfigManager.get("api.nominatim.url");
            String urlStr = baseUrl + java.net.URLEncoder.encode(query, "UTF-8");
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

    @FXML
    public void logout() {
        System.out.println("CustomerDashboardController: Logging out...");
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
        colPrice.setCellFactory(tc -> new TableCell<Ride, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f / seat", price));
                }
            }
        });

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

        colBookPayment.setCellValueFactory(cellData -> {
            Request req = cellData.getValue();
            if ("ACCEPTED".equals(req.getStatus()) && "UNPAID".equals(req.getPaymentStatus())) {
                Button payBtn = new Button("Pay ₹" + (req.getRidePrice() * req.getSeatsRequested()));
                payBtn.getStyleClass().add("button-action");
                payBtn.setOnAction(e -> handlePayment(req));
                return new SimpleObjectProperty<>(payBtn);
            } else if ("PAID".equals(req.getPaymentStatus())) {
                return new SimpleObjectProperty<>(new Button("PAID"));
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        colBookCancel.setCellValueFactory(cellData -> {
            Request req = cellData.getValue();
            if ("PENDING".equals(req.getStatus()) || "ACCEPTED".equals(req.getStatus())) {
                Button cancelBtn = new Button("Cancel");
                cancelBtn.getStyleClass().add("button-danger"); // Assuming a danger style exists or just plain button
                cancelBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;"); // Red style
                cancelBtn.setOnAction(e -> handleCancel(req));
                return new SimpleObjectProperty<>(cancelBtn);
            }
            return new SimpleObjectProperty<>(null);
        });
    }

    private void handleCancel(Request req) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Ride");
        alert.setHeaderText("Are you sure you want to cancel this ride request?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (RequestDAO.updateRequestStatus(req.getId(), "CANCELLED")) {

                    if ("PAID".equals(req.getPaymentStatus())) {
                        double amount = req.getRidePrice() * req.getSeatsRequested();

                        EmailService.sendRefundInitiatedEmail(
                                UserSession.getUserEmail(),
                                UserSession.getUserName(),
                                req.getId(),
                                amount);
                    }

                    showAlert("Success", "Ride request cancelled.");
                    refreshBookings();
                } else {
                    showAlert("Error", "Failed to cancel ride request.");
                }
            }
        });
    }

    private void handlePayment(Request req) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/nichi/carpoolingapp/payment-dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            PaymentController controller = loader.getController();
            controller.setRequest(req);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Payment - Request ID: " + req.getId());
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isPaymentSuccessful()) {
                refreshBookings();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open payment dialog.");
        }
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
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Request Ride");
        dialog.setHeaderText("Ride from " + ride.getSource() + " to " + ride.getDestination());
        dialog.setContentText("Enter number of seats (Available: " + ride.getSeats() + "):");

        dialog.showAndWait().ifPresent(seatsStr -> {
            try {
                int requestedSeats = Integer.parseInt(seatsStr);

                if (requestedSeats <= 0) {
                    showAlert("Error", "Please enter a valid number of seats.");
                    return;
                }

                if (requestedSeats > ride.getSeats()) {
                    showAlert("Error", "Only " + ride.getSeats() + " seats available.");
                    return;
                }

                Request req = new Request(ride.getId(), UserSession.getUserId(), "PENDING");
                req.setSeatsRequested(requestedSeats);

                if (RequestDAO.createRequest(req)) {
                    showAlert("Success", "Ride requested successfully for " + requestedSeats + " seats!");
                    refreshBookings();
                } else {
                    showAlert("Error", "Failed to request ride.");
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid seat number.");
            }
        });
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

    private void updateMapMarker(String location, boolean isSource) {
        if (mapWebView != null) {
            String script = String.format("searchLocation('%s', %b)", location.replace("'", "\\'"), isSource);
            mapWebView.getEngine().executeScript(script);
        }
    }
}
