package com.nichi.carpoolingapp.controller;

import com.nichi.carpoolingapp.dao.PaymentDAO;
import com.nichi.carpoolingapp.model.Payment;
import com.nichi.carpoolingapp.model.Request;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.nichi.carpoolingapp.UserSession;
import com.nichi.carpoolingapp.MainApp;
import com.nichi.carpoolingapp.service.StripeService;
import javafx.scene.control.Hyperlink;
import java.util.UUID;
import java.net.URI;

public class PaymentController {

    @FXML
    private Label amountLabel;
    @FXML
    private ComboBox<String> methodCombo;
    @FXML
    private VBox detailsBox;
    @FXML
    private TextField detailsField;
    @FXML
    private VBox stripeBox;
    @FXML
    private Hyperlink stripeLink;

    private Request request;
    private boolean paymentSuccessful = false;

    @FXML
    public void initialize() {
        methodCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCash = "Cash (Pay to Driver)".equals(newVal);
            boolean isStripe = "Stripe".equals(newVal);

            detailsBox.setVisible(!isCash && !isStripe && newVal != null);
            stripeBox.setVisible(isStripe);
        });
    }

    public void setRequest(Request request) {
        this.request = request;
        double totalAmount = request.getRidePrice() * request.getSeatsRequested();
        amountLabel.setText(String.format("₹ %.2f (%d person%s)", totalAmount, request.getSeatsRequested(),
                request.getSeatsRequested() > 1 ? "s" : ""));
    }

    public double getTotalAmount() {
        return request.getRidePrice() * request.getSeatsRequested();
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    @FXML
    private void processPayment() {
        String method = methodCombo.getValue();
        if (method == null) {
            showAlert("Error", "Please select a payment method.");
            return;
        }

        if ("Stripe".equals(method)) {
            generateStripeLink();
            return;
        }

        if (!method.equals("Cash (Pay to Driver)") && detailsField.getText().isEmpty()) {
            showAlert("Error", "Please enter payment details.");
            return;
        }

        completePayment(method, "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    private void generateStripeLink() {
        double totalAmount = getTotalAmount();
        String link = StripeService.createCheckoutSession(
                totalAmount,
                String.valueOf(request.getId()),
                UserSession.getUserEmail());

        if (link != null) {
            boolean isMock = link.contains("stripe.com/docs/testing");
            String labelText = isMock ? "DEV MOCK MODE: Simulated Link Generated" : "Stripe Checkout Link Generated!";
            ((Label) stripeBox.getChildren().get(0)).setText(labelText);

            stripeLink.setText(link);
            stripeBox.setVisible(true);

            String alertMsg = isMock
                    ? "Stripe is in MOCK MODE for development. No real keys found in config.properties."
                    : "Stripe link generated. Click the link to complete payment.";
            showAlert("Info", alertMsg);
        } else {
            showAlert("Error", "Failed to generate Stripe link. Please try another method.");
        }
    }

    @FXML
    private void openStripeLink() {
        String url = stripeLink.getText();
        if (url != null && !url.isEmpty()) {
            try {
                MainApp.getInstance().getHostServices().showDocument(url);

                // After opening link, show a confirmation dialog
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Payment Confirmation");
                confirm.setHeaderText("Did you complete the payment?");
                confirm.setContentText("Click OK if you have successfully paid via Stripe.");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        completePayment("Stripe", "STP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Could not open browser. Please copy and paste the link manually: " + url);
            }
        }
    }

    private void completePayment(String method, String transactionId) {
        double totalAmount = getTotalAmount();
        Payment payment = new Payment(request.getId(), totalAmount, method, "PAID", transactionId);

        if (PaymentDAO.createPayment(payment)) {
            paymentSuccessful = true;
            showAlert("Success", "Payment processed successfully!\nTransaction ID: " + transactionId);
            closeWindow();
        } else {
            showAlert("Error", "Failed to process payment. Please try again.");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) amountLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
