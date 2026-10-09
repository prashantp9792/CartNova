package com.cartnova.dao;

import com.cartnova.model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO extends BaseDAO {

    private final ProductDAO productDAO = new ProductDAO();

    public boolean addItem(int userId, int productId, int quantity) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CartDAO.addItem error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean updateQuantity(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            return removeItem(userId, productId);
        }
        String sql = "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CartDAO.updateQuantity error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean removeItem(int userId, int productId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CartDAO.removeItem error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("CartDAO.clearCart error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public List<CartItem> findByUserId(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE user_id = ? ORDER BY added_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setUserId(rs.getInt("user_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setAddedAt(rs.getTimestamp("added_at"));

                productDAO.findById(item.getProductId()).ifPresent(item::setProduct);
                if (item.getProduct() != null && item.getProduct().isActive()) {
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("CartDAO.findByUserId error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public int getCartItemCount(int userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("CartDAO.getCartItemCount error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }
}
