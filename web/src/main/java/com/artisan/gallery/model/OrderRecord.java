package com.artisan.gallery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String userEmail;
    private String productName;
    private Double price;
    private LocalDateTime orderDate;

    private String address;
    private String paymentMethod;
    private String status; // PLACED, SHIPPED, DELIVERED, CANCELLED
    private String imageUrl;

    public OrderRecord() {}

    public OrderRecord(String userName, String userEmail, String productName, Double price, LocalDateTime orderDate, String address, String paymentMethod, String status, String imageUrl) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.productName = productName;
        this.price = price;
        this.orderDate = orderDate;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
