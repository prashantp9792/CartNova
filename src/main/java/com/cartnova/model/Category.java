package com.cartnova.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private boolean active;
    private Timestamp createdAt;

    public Category() {
        this.active = true;
    }

    public Category(int categoryId, String name, String description, String imageUrl) {
        this();
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
