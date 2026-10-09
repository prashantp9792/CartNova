package com.cartnova.dao;

import com.cartnova.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO extends BaseDAO {

    /**
     * Executes an ACID Transactional Order Placement.
     * 1. Creates order record in `orders` table.
     * 2. Validates product stock & creates `order_items` records.
     * 3. Decrements product `stock_quantity`.
     * 4. Creates `payments` record.
     * 5. Clears buyer's `cart_items`.
     * 6. Commits transaction. Rollbacks cleanly on any failure.
     */
    public boolean placeOrder(Order order, List<CartItem> cartItems, Payment.Method paymentMethod) throws SQLException {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }

        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        PreparedStatement stockStmt = null;
        PreparedStatement paymentStmt = null;
        PreparedStatement clearCartStmt = null;
        ResultSet rs = null;

        String insertOrderSql = "INSERT INTO orders (user_id, total_amount, status, shipping_address) VALUES (?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, seller_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";
        String insertPaymentSql = "INSERT INTO payments (order_id, payment_method, payment_status, amount, transaction_ref) VALUES (?, ?, ?, ?, ?)";
        String clearCartSql = "DELETE FROM cart_items WHERE user_id = ?";

        try {
            conn = getConnection();
            // START JDBC TRANSACTION
            conn.setAutoCommit(false);

            // 1. Insert Order
            orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setBigDecimal(2, order.getTotalAmount());
            orderStmt.setString(3, order.getStatus().name());
            orderStmt.setString(4, order.getShippingAddress());

            int affected = orderStmt.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }

            rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
                order.setOrderId(orderId);
            } else {
                conn.rollback();
                return false;
            }

            // 2. Insert Order Items & Update Stock
            itemStmt = conn.prepareStatement(insertItemSql);
            stockStmt = conn.prepareStatement(updateStockSql);

            for (CartItem item : cartItems) {
                Product p = item.getProduct();
                if (p == null || p.getStockQuantity() < item.getQuantity()) {
                    throw new SQLException("Insufficient stock for product: " + (p != null ? p.getName() : "Unknown"));
                }

                // Add to order items batch
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, p.getProductId());
                itemStmt.setInt(3, p.getSellerId());
                itemStmt.setInt(4, item.getQuantity());
                itemStmt.setBigDecimal(5, p.getPrice());
                itemStmt.addBatch();

                // Deduct stock atomically
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, p.getProductId());
                stockStmt.setInt(3, item.getQuantity());
                int stockUpdated = stockStmt.executeUpdate();
                if (stockUpdated == 0) {
                    throw new SQLException("Stock changed concurrently for product: " + p.getName());
                }
            }
            itemStmt.executeBatch();

            // 3. Insert Payment
            String txnRef = "TXN_" + paymentMethod.name() + "_" + System.currentTimeMillis();
            paymentStmt = conn.prepareStatement(insertPaymentSql);
            paymentStmt.setInt(1, orderId);
            paymentStmt.setString(2, paymentMethod.name());
            // Checkout records a payment preference; no payment gateway captures funds.
            paymentStmt.setString(3, Payment.Status.PENDING.name());
            paymentStmt.setBigDecimal(4, order.getTotalAmount());
            paymentStmt.setString(5, txnRef);
            paymentStmt.executeUpdate();

            // 4. Clear Cart
            clearCartStmt = conn.prepareStatement(clearCartSql);
            clearCartStmt.setInt(1, order.getUserId());
            clearCartStmt.executeUpdate();

            // COMMIT TRANSACTION
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // ROLLBACK ON ERROR
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            close(null, orderStmt, rs);
            close(null, itemStmt);
            close(null, stockStmt);
            close(null, paymentStmt);
            close(null, clearCartStmt);
            close(conn, null);
        }
    }

    public Optional<Order> findById(int orderId) {
        String sql = "SELECT o.*, CONCAT(u.first_name, ' ', u.last_name) AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.order_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItemsByOrderId(conn, orderId));
                findPaymentByOrderId(conn, orderId).ifPresent(order::setPayment);
                return Optional.of(order);
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.findById error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return Optional.empty();
    }

    public List<Order> findByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, CONCAT(u.first_name, ' ', u.last_name) AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.user_id = ? ORDER BY o.order_date DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItemsByOrderId(conn, order.getOrderId()));
                findPaymentByOrderId(conn, order.getOrderId()).ifPresent(order::setPayment);
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.findByUserId error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public List<Order> findBySellerId(int sellerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT DISTINCT o.*, CONCAT(u.first_name, ' ', u.last_name) AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "WHERE oi.seller_id = ? ORDER BY o.order_date DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItemsForSeller(conn, order.getOrderId(), sellerId));
                findPaymentByOrderId(conn, order.getOrderId()).ifPresent(order::setPayment);
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.findBySellerId error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, CONCAT(u.first_name, ' ', u.last_name) AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id ORDER BY o.order_date DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findOrderItemsByOrderId(conn, order.getOrderId()));
                findPaymentByOrderId(conn, order.getOrderId()).ifPresent(order::setPayment);
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.findAll error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    public boolean updateStatus(int orderId, Order.Status status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("OrderDAO.updateStatus error: " + e.getMessage());
        } finally {
            close(conn, stmt);
        }
        return false;
    }

    public boolean updateStatusForSeller(int orderId, int sellerId, Order.Status status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ? AND EXISTS " +
                     "(SELECT 1 FROM order_items WHERE order_id = ? AND seller_id = ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);
            stmt.setInt(3, orderId);
            stmt.setInt(4, sellerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("OrderDAO.updateStatusForSeller error: " + e.getMessage());
            return false;
        }
    }

    public BigDecimal getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status != 'CANCELLED'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("OrderDAO.getTotalRevenue error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSellerRevenue(int sellerId) {
        String sql = "SELECT COALESCE(SUM(oi.price_at_purchase * oi.quantity), 0) " +
                     "FROM order_items oi JOIN orders o ON oi.order_id = o.order_id " +
                     "WHERE oi.seller_id = ? AND o.status != 'CANCELLED'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            rs = stmt.executeQuery();
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("OrderDAO.getSellerRevenue error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return BigDecimal.ZERO;
    }

    public int countOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("OrderDAO.countOrders error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    public int countOrdersBySeller(int sellerId) {
        String sql = "SELECT COUNT(DISTINCT oi.order_id) FROM order_items oi WHERE oi.seller_id = ?";
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
            System.err.println("OrderDAO.countOrdersBySeller error: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    private List<OrderItem> findOrderItemsByOrderId(Connection conn, int orderId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name, p.image_url AS product_image, " +
                     "CONCAT(u.first_name, ' ', u.last_name) AS seller_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "JOIN users u ON oi.seller_id = u.user_id " +
                     "WHERE oi.order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setSellerId(rs.getInt("seller_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setSellerName(rs.getString("seller_name"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    private List<OrderItem> findOrderItemsForSeller(Connection conn, int orderId, int sellerId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name, p.image_url AS product_image, " +
                     "CONCAT(u.first_name, ' ', u.last_name) AS seller_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "JOIN users u ON oi.seller_id = u.user_id " +
                     "WHERE oi.order_id = ? AND oi.seller_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setSellerId(rs.getInt("seller_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setSellerName(rs.getString("seller_name"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    private Optional<Payment> findPaymentByOrderId(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        }
        return Optional.empty();
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(Order.Status.valueOf(rs.getString("status")));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            o.setBuyerName(rs.getString("buyer_name"));
            o.setBuyerEmail(rs.getString("buyer_email"));
        } catch (SQLException ignored) {}
        return o;
    }
}
