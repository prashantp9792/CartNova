package com.cartnova.dao;

import com.cartnova.model.WishlistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO extends BaseDAO {

    private final ProductDAO productDAO = new ProductDAO();

    public boolean add(int userId, int productId) {
        String sql = "INSERT IGNORE INTO wishlist_items (user_id, product_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("WishlistDAO.add error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean remove(int userId, int productId) {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("WishlistDAO.remove error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean isInWishlist(int userId, int productId) {
        String sql = "SELECT 1 FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("WishlistDAO.isInWishlist error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return false;
    }

    public List<WishlistItem> findByUserId(int userId) {
        List<WishlistItem> list = new ArrayList<>();
        String sql = "SELECT * FROM wishlist_items WHERE user_id = ? ORDER BY added_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                WishlistItem item = new WishlistItem();
                item.setWishlistItemId(rs.getInt("wishlist_item_id"));
                item.setUserId(rs.getInt("user_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setAddedAt(rs.getTimestamp("added_at"));

                productDAO.findById(item.getProductId()).ifPresent(item::setProduct);
                if (item.getProduct() != null) {
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("WishlistDAO.findByUserId error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }
}
