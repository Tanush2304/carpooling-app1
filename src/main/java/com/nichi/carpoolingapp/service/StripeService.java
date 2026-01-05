package com.nichi.carpoolingapp.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.nichi.carpoolingapp.util.ConfigManager;

public class StripeService {

    static {
        String secretKey = ConfigManager.get("stripe.secret_key", "");
        if (!secretKey.isEmpty() && !secretKey.contains("your_key")) {
            Stripe.apiKey = secretKey;
        }
    }

    public static String createCheckoutSession(double amount, String requestId, String userEmail) {
        String secretKey = ConfigManager.get("stripe.secret_key", "");

        // Mock Mode Check
        if (secretKey.isEmpty() || secretKey.contains("your_key")) {
            System.out.println(">>> [STRIPE MOCK MODE] Generating simulated checkout link for " + userEmail);
            // Return a stripe test documentation link as a mock
            return "https://stripe.com/docs/testing";
        }

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://example.com/success")
                    .setCancelUrl("https://example.com/cancel")
                    .setCustomerEmail(userEmail)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount((long) (amount * 100)) // Amount in paise
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(
                                                                            "Ride Payment (Request #" + requestId + ")")
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
