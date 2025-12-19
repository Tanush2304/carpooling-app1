package com.nichi.carpoolingapp.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Ride {
    private int id;
    private int driverId;
    private String source;
    private String destination;
    private Date rideDate;
    private Time rideTime;
    private int seats;
    private double price;
    private String status;
    private String idProofPath;
    private String carPhotoPath;
    private Timestamp createdAt;

    public Ride() {
    }

    public Ride(int driverId, String source, String destination, Date rideDate, Time rideTime, int seats, double price,
            String status, String idProofPath, String carPhotoPath) {
        this.driverId = driverId;
        this.source = source;
        this.destination = destination;
        this.rideDate = rideDate;
        this.rideTime = rideTime;
        this.seats = seats;
        this.price = price;
        this.status = status;
        this.idProofPath = idProofPath;
        this.carPhotoPath = carPhotoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getRideDate() {
        return rideDate;
    }

    public void setRideDate(Date rideDate) {
        this.rideDate = rideDate;
    }

    public Time getRideTime() {
        return rideTime;
    }

    public void setRideTime(Time rideTime) {
        this.rideTime = rideTime;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdProofPath() {
        return idProofPath;
    }

    public void setIdProofPath(String idProofPath) {
        this.idProofPath = idProofPath;
    }

    public String getCarPhotoPath() {
        return carPhotoPath;
    }

    public void setCarPhotoPath(String carPhotoPath) {
        this.carPhotoPath = carPhotoPath;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
