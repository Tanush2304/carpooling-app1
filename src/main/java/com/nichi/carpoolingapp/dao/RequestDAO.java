package com.nichi.carpoolingapp.dao;

import com.nichi.carpoolingapp.model.Request;
import com.nichi.carpoolingapp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public static boolean createRequest(Request request) {
        String sql = "INSERT INTO requests (ride_id, user_id, status) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, request.getRideId());
            ps.setInt(2, request.getUserId());
            ps.setString(3, request.getStatus());

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Request> getRequestsByUserId(int userId) {
        List<Request> requests = new ArrayList<>();
        // Join with rides and users (drivers) to get details
        String sql = "SELECT req.*, r.source, r.destination, r.ride_date, u.name as driver_name " +
                "FROM requests req " +
                "JOIN rides r ON req.ride_id = r.id " +
                "JOIN users u ON r.driver_id = u.id " +
                "WHERE req.user_id = ? ORDER BY req.requested_at DESC";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Request req = new Request();
                req.setId(rs.getInt("id"));
                req.setRideId(rs.getInt("ride_id"));
                req.setUserId(rs.getInt("user_id"));
                req.setStatus(rs.getString("status"));
                req.setRequestedAt(rs.getTimestamp("requested_at"));

                // Populate transient fields
                req.setRideSource(rs.getString("source"));
                req.setRideDestination(rs.getString("destination"));
                req.setRideDate(rs.getDate("ride_date"));
                req.setDriverName(rs.getString("driver_name"));

                requests.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    public static List<Request> getRequestsByRideId(int rideId) {
        List<Request> requests = new ArrayList<>();
        // Join with users table to get the customer name
        String sql = "SELECT r.*, u.name as customer_name FROM requests r JOIN users u ON r.user_id = u.id WHERE r.ride_id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rideId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Request req = new Request();
                req.setId(rs.getInt("id"));
                req.setRideId(rs.getInt("ride_id"));
                req.setUserId(rs.getInt("user_id"));
                req.setStatus(rs.getString("status"));
                req.setRequestedAt(rs.getTimestamp("requested_at"));
                req.setCustomerName(rs.getString("customer_name")); // Set the name
                requests.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    public static boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE requests SET status = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // For driver to verify request is accepted
    public static Request getRequestById(int id) {
        String sql = "SELECT * FROM requests WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Request req = new Request();
                req.setId(rs.getInt("id"));
                req.setRideId(rs.getInt("ride_id"));
                req.setUserId(rs.getInt("user_id"));
                req.setStatus(rs.getString("status"));
                req.setRequestedAt(rs.getTimestamp("requested_at"));
                return req;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
