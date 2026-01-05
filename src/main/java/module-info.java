module com.nichi.carpoolingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires java.sql;
    requires java.mail;
    requires io.github.cdimascio.dotenv.java;
    requires java.desktop;
    requires stripe.java;

    opens com.nichi.carpoolingapp to javafx.fxml;

    exports com.nichi.carpoolingapp;
    exports com.nichi.carpoolingapp.controller;

    opens com.nichi.carpoolingapp.controller to javafx.fxml;
    opens com.nichi.carpoolingapp.model to javafx.base;
    opens com.nichi.carpoolingapp.dao to javafx.base;
}