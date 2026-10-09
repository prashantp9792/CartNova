package com.cartnova.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        ADMIN, SELLER, BUYER
    }

    private int userId;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.role = Role.BUYER;
        this.active = true;
    }

    public User(String email, String passwordHash, String firstName, String lastName, Role role) {
        this();
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null && !addressLine1.isEmpty()) sb.append(addressLine1);
        if (addressLine2 != null && !addressLine2.isEmpty()) sb.append(", ").append(addressLine2);
        if (city != null && !city.isEmpty()) sb.append(", ").append(city);
        if (state != null && !state.isEmpty()) sb.append(", ").append(state);
        if (zipCode != null && !zipCode.isEmpty()) sb.append(" - ").append(zipCode);
        return sb.toString();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", email='" + email + "', role=" + role + '}';
    }
}
