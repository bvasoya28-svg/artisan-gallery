package com.artisan.gallery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String userName;
    private String userEmail;
    private int rating;
    @Column(length = 1000)
    private String comment;
    private String imageUrl;
    private LocalDateTime date;

    public Review() {}

    public Review(Long productId, String userName, String userEmail, int rating, String comment, String imageUrl, LocalDateTime date) {
        this.productId = productId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
