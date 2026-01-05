package com.nichi.carpoolingapp.dao;

import com.nichi.carpoolingapp.model.Payment;
import com.nichi.carpoolingapp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaymentDAO {

    public static boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (request_id, amount, payment_method, status, transaction_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, payment.getRequestId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMethod());
            ps.setString(4, payment.getStatus());
            ps.setString(5, payment.getTransactionId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Payment getPaymentByRequestId(int requestId) {
        String sql = "SELECT * FROM payments WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setRequestId(rs.getInt("request_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setStatus(rs.getString("status"));
                payment.setTransactionId(rs.getString("transaction_id"));
                payment.setPaymentDate(rs.getTimestamp("payment_date"));
                return payment;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
