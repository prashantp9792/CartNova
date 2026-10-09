package com.cartnova.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Method {
        COD, UPI, NET_BANKING, CARD
    }

    public enum Status {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    private int paymentId;
    private int orderId;
    private Method paymentMethod;
    private Status paymentStatus;
    private BigDecimal amount;
    private String transactionRef;
    private Timestamp paidAt;

    public Payment() {
        this.paymentStatus = Status.PENDING;
        this.paymentMethod = Method.COD;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Method getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Method paymentMethod) { this.paymentMethod = paymentMethod; }

    public Status getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Status paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }
}
