package com.cartnova.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class WishlistItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int wishlistItemId;
    private int userId;
    private int productId;
    private Timestamp addedAt;

    private Product product;

    public WishlistItem() {}

    public int getWishlistItemId() { return wishlistItemId; }
    public void setWishlistItemId(int wishlistItemId) { this.wishlistItemId = wishlistItemId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
