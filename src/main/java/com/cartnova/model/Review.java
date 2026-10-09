package com.cartnova.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private int reviewId;
    private int userId;
    private int productId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    // View join
    private String userName;

    public Review() {
        this.rating = 5;
    }

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
