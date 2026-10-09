package com.cartnova.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    private int orderId;
    private int userId;
    private BigDecimal totalAmount;
    private Status status;
    private String shippingAddress;
    private Timestamp orderDate;
    private Timestamp updatedAt;

    // View joins
    private String buyerName;
    private String buyerEmail;
    private List<OrderItem> items;
    private Payment payment;

    public Order() {
        this.status = Status.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public String getStatusBadgeClass() {
        if (status == null) return "bg-gray-100 text-gray-800";
        switch (status) {
            case DELIVERED: return "bg-green-100 text-green-800";
            case SHIPPED: return "bg-blue-100 text-blue-800";
            case CONFIRMED: return "bg-purple-100 text-purple-800";
            case PENDING: return "bg-yellow-100 text-yellow-800";
            case CANCELLED: return "bg-red-100 text-red-800";
            default: return "bg-gray-100 text-gray-800";
        }
    }
}
