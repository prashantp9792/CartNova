package com.cartnova.dao;

import com.cartnova.model.Payment;

import java.sql.*;
import java.util.Optional;

public class PaymentDAO extends BaseDAO {

    public boolean save(Payment payment) {
        String sql = "INSERT INTO payments (order_id, payment_method, payment_status, amount, transaction_ref) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, payment.getOrderId());
            stmt.setString(2, payment.getPaymentMethod().name());
            stmt.setString(3, payment.getPaymentStatus().name());
            stmt.setBigDecimal(4, payment.getAmount());
            stmt.setString(5, payment.getTransactionRef());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PaymentDAO.save error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public Optional<Payment> findByOrderId(int orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("payment_id"));
                p.setOrderId(rs.getInt("order_id"));
                p.setPaymentMethod(Payment.Method.valueOf(rs.getString("payment_method")));
                p.setPaymentStatus(Payment.Status.valueOf(rs.getString("payment_status")));
                p.setAmount(rs.getBigDecimal("amount"));
                p.setTransactionRef(rs.getString("transaction_ref"));
                p.setPaidAt(rs.getTimestamp("paid_at"));
                return Optional.of(p);
            }
        } catch (SQLException e) {
            System.err.println("PaymentDAO.findByOrderId error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return Optional.empty();
    }
}
