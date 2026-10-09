package com.cartnova.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class DashboardStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalUsers;
    private int totalSellers;
    private int totalBuyers;
    private int totalProducts;
    private int totalCategories;
    private int totalOrders;
    private int pendingOrders;
    private int completedOrders;
    private BigDecimal totalRevenue;

    public DashboardStats() {
        this.totalRevenue = BigDecimal.ZERO;
    }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalSellers() { return totalSellers; }
    public void setTotalSellers(int totalSellers) { this.totalSellers = totalSellers; }

    public int getTotalBuyers() { return totalBuyers; }
    public void setTotalBuyers(int totalBuyers) { this.totalBuyers = totalBuyers; }

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public int getTotalCategories() { return totalCategories; }
    public void setTotalCategories(int totalCategories) { this.totalCategories = totalCategories; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public int getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }

    public int getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(int completedOrders) { this.completedOrders = completedOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
