package com.cartnova.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int cartItemId;
    private int userId;
    private int productId;
    private int quantity;
    private Timestamp addedAt;

    private Product product;

    public CartItem() {
        this.quantity = 1;
    }

    public int getCartItemId() { return cartItemId; }
    public void setCartItemId(int cartItemId) { this.cartItemId = cartItemId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getSubtotal() {
        if (product != null && product.getPrice() != null) {
            return product.getPrice().multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}
