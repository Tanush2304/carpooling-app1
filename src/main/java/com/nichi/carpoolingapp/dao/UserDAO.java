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
}
