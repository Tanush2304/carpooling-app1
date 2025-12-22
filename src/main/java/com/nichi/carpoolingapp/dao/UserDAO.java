package com.nichi.carpoolingapp.dao;

import com.nichi.carpoolingapp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public static boolean registerUser(String name,
            String email,
            String password) {

        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginUser(String email,
            String password) {

        String sql = "SELECT * FROM users WHERE email=? AND password=? ";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean emailExists(String email) {

        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean userExists(String email) {

        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUserNameByEmail(String email) {
        String sql = "SELECT name FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("name");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "User";
    }

    public static int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getEmailById(int userId) {
        String sql = "SELECT email FROM users WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("email");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateDriverDocuments(int userId, String licensePath, String carPath) {
        String sql = "UPDATE users SET license_path=?, car_photo_path=?, driver_status='PENDING' WHERE id=?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, licensePath);
            ps.setString(2, carPath);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getDriverStatus(int userId) {
        String sql = "SELECT driver_status FROM users WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("driver_status");
                return (status == null) ? "NONE" : status;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NONE";
    }

    // For Admin Dashboard
    public static class PendingDriver {
        public int id;
        public String name;
        public String email;
        public String licensePath;
        public String carPath;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getLicensePath() {
            return licensePath;
        }

        public String getCarPath() {
            return carPath;
        }
    }

    public static java.util.List<PendingDriver> getPendingDrivers() {
        java.util.List<PendingDriver> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users WHERE driver_status = 'PENDING'";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PendingDriver pd = new PendingDriver();
                pd.id = rs.getInt("id");
                pd.name = rs.getString("name");
                pd.email = rs.getString("email");
                pd.licensePath = rs.getString("license_path");
                pd.carPath = rs.getString("car_photo_path");
                list.add(pd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateDriverStatus(int userId, String status) {
        String sql = "UPDATE users SET driver_status=? WHERE id=?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
