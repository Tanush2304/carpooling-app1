package com.nichi.carpoolingapp.dao;

import com.nichi.carpoolingapp.model.Ride;
import com.nichi.carpoolingapp.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RideDAO {

    public static boolean publishRide(Ride ride) {
        String sql = "INSERT INTO rides (driver_id, source, destination, ride_date, ride_time, seats, price, status, id_proof_path, car_photo_path) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ride.getDriverId());
            ps.setString(2, ride.getSource());
            ps.setString(3, ride.getDestination());
            ps.setDate(4, ride.getRideDate());
            ps.setTime(5, ride.getRideTime());
            ps.setInt(6, ride.getSeats());
            ps.setDouble(7, ride.getPrice());
            ps.setString(8, ride.getStatus());
            ps.setString(9, ride.getIdProofPath());
            ps.setString(10, ride.getCarPhotoPath());

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRideStatus(int rideId, String status) {
        String sql = "UPDATE rides SET status = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, rideId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Ride> getRidesByDriverId(int driverId) {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM rides WHERE driver_id = ? ORDER BY created_at DESC";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ride ride = mapResultSetToRide(rs);
                rides.add(ride);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rides;
    }

    public static List<Ride> searchRides(String source, String destination, Date date) {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM rides WHERE source LIKE ? AND destination LIKE ? AND ride_date = ? AND status = 'OPEN'";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + source + "%");
            ps.setString(2, "%" + destination + "%");
            ps.setDate(3, date);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ride ride = mapResultSetToRide(rs);
                rides.add(ride);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rides;
    }

    private static Ride mapResultSetToRide(ResultSet rs) throws Exception {
        Ride ride = new Ride();
        ride.setId(rs.getInt("id"));
        ride.setDriverId(rs.getInt("driver_id"));
        ride.setSource(rs.getString("source"));
        ride.setDestination(rs.getString("destination"));
        ride.setRideDate(rs.getDate("ride_date"));
        ride.setRideTime(rs.getTime("ride_time"));
        ride.setSeats(rs.getInt("seats"));
        ride.setPrice(rs.getDouble("price"));
        ride.setStatus(rs.getString("status"));
        ride.setIdProofPath(rs.getString("id_proof_path"));
        ride.setCarPhotoPath(rs.getString("car_photo_path"));
        ride.setCreatedAt(rs.getTimestamp("created_at"));
        return ride;
    }
}
