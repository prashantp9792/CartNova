package com.cartnova.dao;

import com.cartnova.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO extends BaseDAO {

    public boolean save(Product p) {
        String sql = "INSERT INTO products (seller_id, category_id, name, description, price, stock_quantity, image_url, brand, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, p.getSellerId());
            stmt.setInt(2, p.getCategoryId());
            stmt.setString(3, p.getName());
            stmt.setString(4, p.getDescription());
            stmt.setBigDecimal(5, p.getPrice());
            stmt.setInt(6, p.getStockQuantity());
            stmt.setString(7, p.getImageUrl());
            stmt.setString(8, p.getBrand());
            stmt.setBoolean(9, p.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    p.setProductId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ProductDAO.save error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return false;
    }

    public boolean update(Product p) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, price = ?, stock_quantity = ?, image_url = ?, brand = ?, is_active = ? " +
                     "WHERE product_id = ? AND seller_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, p.getCategoryId());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getDescription());
            stmt.setBigDecimal(4, p.getPrice());
            stmt.setInt(5, p.getStockQuantity());
            stmt.setString(6, p.getImageUrl());
            stmt.setString(7, p.getBrand());
            stmt.setBoolean(8, p.isActive());
            stmt.setInt(9, p.getProductId());
            stmt.setInt(10, p.getSellerId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.update error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean adminUpdate(Product p) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, price = ?, stock_quantity = ?, image_url = ?, brand = ?, is_active = ? " +
                     "WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, p.getCategoryId());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getDescription());
            stmt.setBigDecimal(4, p.getPrice());
            stmt.setInt(5, p.getStockQuantity());
            stmt.setString(6, p.getImageUrl());
            stmt.setString(7, p.getBrand());
            stmt.setBoolean(8, p.isActive());
            stmt.setInt(9, p.getProductId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.adminUpdate error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean updateStock(int productId, int newStock) {
        String sql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.updateStock error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean delete(int productId, int sellerId) {
        String sql = "DELETE FROM products WHERE product_id = ? AND seller_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            stmt.setInt(2, sellerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ProductDAO.delete error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public Optional<Product> findById(int productId) {
        String sql = "SELECT p.*, c.name AS category_name, CONCAT(u.first_name, ' ', u.last_name) AS seller_name, " +
                     "COALESCE(AVG(r.rating), 0) AS avg_rating, COUNT(r.review_id) AS review_count " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN users u ON p.seller_id = u.user_id " +
                     "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                     "WHERE p.product_id = ? " +
                     "GROUP BY p.product_id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("ProductDAO.findById error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return Optional.empty();
    }

    public List<Product> findBySeller(int sellerId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name, CONCAT(u.first_name, ' ', u.last_name) AS seller_name, " +
                     "COALESCE(AVG(r.rating), 0) AS avg_rating, COUNT(r.review_id) AS review_count " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN users u ON p.seller_id = u.user_id " +
                     "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                     "WHERE p.seller_id = ? " +
                     "GROUP BY p.product_id ORDER BY p.created_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("ProductDAO.findBySeller error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public List<Product> searchProducts(String keyword, Integer categoryId, Double minPrice, Double maxPrice, String sortBy) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.name AS category_name, CONCAT(u.first_name, ' ', u.last_name) AS seller_name, " +
            "COALESCE(AVG(r.rating), 0) AS avg_rating, COUNT(r.review_id) AS review_count " +
            "FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "JOIN users u ON p.seller_id = u.user_id " +
            "LEFT JOIN reviews r ON p.product_id = r.product_id " +
            "WHERE p.is_active = TRUE "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.brand) LIKE ?) ");
            String k = "%" + keyword.trim().toLowerCase() + "%";
            params.add(k);
            params.add(k);
            params.add(k);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice >= 0) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice > 0) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }

        sql.append("GROUP BY p.product_id ");

        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price ASC ");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price DESC ");
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY avg_rating DESC ");
        } else {
            sql.append("ORDER BY p.created_at DESC ");
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("ProductDAO.searchProducts error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public List<Product> findAllAdmin() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name, CONCAT(u.first_name, ' ', u.last_name) AS seller_name, " +
                     "COALESCE(AVG(r.rating), 0) AS avg_rating, COUNT(r.review_id) AS review_count " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN users u ON p.seller_id = u.user_id " +
                     "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                     "GROUP BY p.product_id ORDER BY p.created_at DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("ProductDAO.findAllAdmin error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("ProductDAO.countAll error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    public int countBySeller(int sellerId) {
        String sql = "SELECT COUNT(*) FROM products WHERE seller_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("ProductDAO.countBySeller error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setSellerId(rs.getInt("seller_id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setImageUrl(rs.getString("image_url"));
        p.setBrand(rs.getString("brand"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            p.setCategoryName(rs.getString("category_name"));
            p.setSellerName(rs.getString("seller_name"));
            p.setAverageRating(rs.getDouble("avg_rating"));
            p.setReviewCount(rs.getInt("review_count"));
        } catch (SQLException ignored) {}

        return p;
    }
}
