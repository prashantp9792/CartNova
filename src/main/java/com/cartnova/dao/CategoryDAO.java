package com.cartnova.dao;

import com.cartnova.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO extends BaseDAO {

    public boolean save(Category category) {
        String sql = "INSERT INTO categories (name, description, image_url, is_active) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getImageUrl());
            stmt.setBoolean(4, category.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    category.setCategoryId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("CategoryDAO.save error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return false;
    }

    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, image_url = ?, is_active = ? WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getImageUrl());
            stmt.setBoolean(4, category.isActive());
            stmt.setInt(5, category.getCategoryId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CategoryDAO.update error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CategoryDAO.delete error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public Optional<Category> findById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, categoryId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("CategoryDAO.findById error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return Optional.empty();
    }

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("CategoryDAO.findAll error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public List<Category> findAllActive() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = TRUE ORDER BY name ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("CategoryDAO.findAllActive error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setImageUrl(rs.getString("image_url"));
        c.setActive(rs.getBoolean("is_active"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
