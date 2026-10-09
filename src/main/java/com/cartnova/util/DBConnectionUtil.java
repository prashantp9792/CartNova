package com.cartnova.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage Database connections via JNDI DataSource (Tomcat DBCP)
 * with a automatic fallback to direct JDBC DriverManager for local standalone testing.
 */
public class DBConnectionUtil {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/cartnova?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private static DataSource dataSource = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/CartNovaDB");
        } catch (Exception e) {
            // JNDI lookup failed (e.g. running outside Tomcat container in standalone unit test)
            // Will fallback to DriverManager in getConnection()
            dataSource = null;
        }
    }

    /**
     * Obtains a database connection.
     * Uses JNDI DataSource if available; otherwise uses DriverManager fallback.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                System.err.println("JNDI Connection failed, falling back to DriverManager: " + e.getMessage());
            }
        }

        String url = System.getProperty("db.url", System.getenv().getOrDefault("CARTNOVA_DB_URL", DEFAULT_URL));
        String user = System.getProperty("db.user", System.getenv().getOrDefault("CARTNOVA_DB_USER", DEFAULT_USER));
        String pass = System.getProperty("db.pass", System.getenv().getOrDefault("CARTNOVA_DB_PASS", DEFAULT_PASS));

        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Safely closes a Connection.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Tests database connectivity.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("DB Connection Test Failed: " + e.getMessage());
            return false;
        }
    }
}
