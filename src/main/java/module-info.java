module com.nichi.carpoolingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;

    opens com.nichi.carpoolingapp to javafx.fxml;

    exports com.nichi.carpoolingapp;
    exports com.nichi.carpoolingapp.controller;

    opens com.nichi.carpoolingapp.controller to javafx.fxml;
    opens com.nichi.carpoolingapp.model to javafx.base;
}