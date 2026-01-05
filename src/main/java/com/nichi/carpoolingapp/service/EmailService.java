package com.nichi.carpoolingapp.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    private static final String SENDER_EMAIL = com.nichi.carpoolingapp.util.ConfigManager.get("email.sender");
    private static final String SENDER_PASSWORD = com.nichi.carpoolingapp.util.ConfigManager.get("email.password");

    public static void sendBookingConfirmation(String toEmail, String driverName, String driverContact) {

        if (SENDER_EMAIL.contains("your_email")) {
            System.out.println(">>> [MOCK EMAIL SERVICE] Sending email to " + toEmail);
            System.out.println("Subject: Ride Confirmed!");
            System.out.println("Body: Your ride with " + driverName + " is confirmed. Contact: " + driverContact);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "The Carpooling App Company"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Ride Confirmed: Detailed Information");

            String msgBody = "Dear Customer,\n\n" +
                    "We are pleased to inform you that your ride request has been ACCEPTED.\n\n" +
                    "--------------DRIVER DETAILS-----------------------\n" +
                    "Name    : " + driverName + "\n" +
                    "Email   : " + driverContact + "\n" +
                    "Please coordinate with your driver for the pickup location.\n" +
                    "Thank you for choosing our service!\n\n" +
                    "Best Regards,\nThe Carpooling App Team";

            message.setText(msgBody);

            Transport.send(message);
            System.out.println("Confirmation email sent successfully to " + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendRejectionEmail(String toEmail, String driverName, String rideDetails) {
        if (SENDER_EMAIL.contains("your_email")) {
            System.out.println(">>> [MOCK EMAIL SERVICE] Rejection sent to " + toEmail);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "The Carpooling App Company"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Update on Your Ride Request");

            String msgBody = "Dear Customer,\n\n" +
                    "We regret to inform you that your ride request with driver " + driverName
                    + " has been REJECTED.\n\n" +
                    "Ride Details: " + rideDetails + "\n\n" +
                    "Please search for another ride on our dashboard.\n\n" +
                    "Best Regards,\nThe Carpooling App Team";

            message.setText(msgBody);

            Transport.send(message);
            System.out.println("Rejection email sent successfully to " + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendRefundInitiatedEmail(String toEmail, String userName,
            int requestId, double amount) {

        String subject = "Refund Initiated - Ride Cancellation";
        String body = "Dear " + userName + ",\n\n" +
                "Your ride request (Request ID: " + requestId + ") has been successfully cancelled.\n\n" +
                "We have received your payment of ₹" + String.format("%.2f", amount) + ".\n" +
                "The refund has been initiated and will be credited back to your original " +
                "payment method within 3–5 working days.\n\n" +
                "If you have any questions, feel free to contact our support team.\n\n" +
                "Best Regards,\n" +
                "The Carpooling App Team";

        sendEmail(toEmail, subject, body);
    }

    private static void sendEmail(String toEmail, String subject, String body) {
        if (SENDER_EMAIL.contains("your_email")) {
            System.out.println(">>> [MOCK EMAIL SERVICE] To: " + toEmail + " | Subject: " + subject);
            return;
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "The Carpooling App Company"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendOTPEmail(String toEmail, String otp) {
        String subject = "Email Verification - Carpooling App";
        String body = "Dear User,\n\n" +
                "Thank you for signing up for the Carpooling App.\n" +
                "Your One-Time Password (OTP) for email verification is:\n\n" +
                "        " + otp + "\n\n" +
                "This OTP is valid for 10 minutes. Please do not share it with anyone.\n\n" +
                "Best Regards,\n" +
                "The Carpooling App Team";

        sendEmail(toEmail, subject, body);
    }

    public static void sendDriverVerificationAlert(String adminEmail, String driverName, String licensePath,
            String carPath) {
        String subject = "New Driver Verification Request";
        String body = "Hello Admin,\n\n" +
                "A new user '" + driverName + "' has submitted documents for driver verification.\n" +
                "Please find the attached documents (License and Car Photo) for your review.\n" +
                "If verified, please log in to the Admin Dashboard to APPROVE the driver.\n\n" +
                "Best,\nCarpooling App System";

        if (SENDER_EMAIL.contains("your_email")) {
            System.out.println(">>> [MOCK EMAIL SERVICE] Sending Alert with attachments to " + adminEmail);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "The Carpooling App Company"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(adminEmail));
            message.setSubject(subject);

            // Create multipart
            Multipart multipart = new MimeMultipart();

            // Text part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            // Attachment 1: License
            if (licensePath != null) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(new java.io.File(licensePath));
                multipart.addBodyPart(attachPart);
            }

            // Attachment 2: Car Photo
            if (carPath != null) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(new java.io.File(carPath));
                multipart.addBodyPart(attachPart);
            }

            message.setContent(multipart);
            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
