package com.cartnova.dao;

import com.cartnova.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO extends BaseDAO {

    public boolean save(User user) {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone, role, address_line1, address_line2, city, state, zip_code) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getRole().name());
            stmt.setString(7, user.getAddressLine1());
            stmt.setString(8, user.getAddressLine2());
            stmt.setString(9, user.getCity());
            stmt.setString(10, user.getState());
            stmt.setString(11, user.getZipCode());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.save error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.findByEmail error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return Optional.empty();
    }

    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.findById error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.findAll error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, address_line1 = ?, address_line2 = ?, city = ?, state = ?, zip_code = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getAddressLine1());
            stmt.setString(5, user.getAddressLine2());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getState());
            stmt.setString(8, user.getZipCode());
            stmt.setInt(9, user.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.updateProfile error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.updatePassword error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean toggleActiveStatus(int userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.toggleActiveStatus error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean updateUserRole(int userId, User.Role role) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, role.name());
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.updateUserRole error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public int countByRole(User.Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, role.name());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.countByRole error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.countAll error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setPhone(rs.getString("phone"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setAddressLine1(rs.getString("address_line1"));
        u.setAddressLine2(rs.getString("address_line2"));
        u.setCity(rs.getString("city"));
        u.setState(rs.getString("state"));
        u.setZipCode(rs.getString("zip_code"));
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
