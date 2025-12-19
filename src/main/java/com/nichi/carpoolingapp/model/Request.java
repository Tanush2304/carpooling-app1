package com.nichi.carpoolingapp.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Request {
    private int id;
    private int rideId;
    private int userId;
    private String status;
    private Timestamp requestedAt;

    private String customerName;

    public Request() {
    }

    public Request(int rideId, int userId, String status) {
        this.rideId = rideId;
        this.userId = userId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Transient fields for display in history
    private String rideSource;
    private String rideDestination;
    private Date rideDate;
    private String driverName;

    public String getRideSource() {
        return rideSource;
    }

    public void setRideSource(String rideSource) {
        this.rideSource = rideSource;
    }

    public String getRideDestination() {
        return rideDestination;
    }

    public void setRideDestination(String rideDestination) {
        this.rideDestination = rideDestination;
    }

    public Date getRideDate() {
        return rideDate;
    }

    public void setRideDate(Date rideDate) {
        this.rideDate = rideDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
