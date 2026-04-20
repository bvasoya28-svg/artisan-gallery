package com.artisan.gallery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private Double price;
    private String imageUrl;
    private String category;
    private String artist;
    private Double rating;
    private Integer reviewCount;
    private String deliveryTime;
    private String uploader;
    private boolean inStock = true;
    private String locationType = "National";
    private String specificLocation = "India";

    public Product() {}

    public Product(String name, String description, Double price, String imageUrl, String category, String artist, Double rating, Integer reviewCount, String deliveryTime, String uploader) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.artist = artist;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.deliveryTime = deliveryTime;
        this.uploader = uploader;
        this.inStock = true;
        this.locationType = "National";
        this.specificLocation = "India";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public String getUploader() { return uploader; }
    public void setUploader(String uploader) { this.uploader = uploader; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }
    public String getSpecificLocation() { return specificLocation; }
    public void setSpecificLocation(String specificLocation) { this.specificLocation = specificLocation; }
}
