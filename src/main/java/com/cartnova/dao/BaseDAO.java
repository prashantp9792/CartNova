package com.cartnova.dao;

import com.cartnova.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract Base DAO providing connection management and utility methods.
 * Demonstrates OOP Inheritance and Encapsulation across all Data Access Objects.
 */
public abstract class BaseDAO {

    protected Connection getConnection() throws SQLException {
        return DBConnectionUtil.getConnection();
    }

    protected void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    protected void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
