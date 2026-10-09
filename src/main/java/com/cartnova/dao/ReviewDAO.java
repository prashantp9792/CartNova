package com.cartnova.dao;

import com.cartnova.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO extends BaseDAO {

    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE rating = VALUES(rating), comment = VALUES(comment)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, review.getUserId());
            stmt.setInt(2, review.getProductId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ReviewDAO.addReview error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public List<Review> findByProductId(int productId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(u.first_name, ' ', u.last_name) AS user_name " +
                     "FROM reviews r JOIN users u ON r.user_id = u.user_id " +
                     "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Review r = new Review();
                r.setReviewId(rs.getInt("review_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setProductId(rs.getInt("product_id"));
                r.setRating(rs.getInt("rating"));
                r.setComment(rs.getString("comment"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setUserName(rs.getString("user_name"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO.findByProductId error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }
}
