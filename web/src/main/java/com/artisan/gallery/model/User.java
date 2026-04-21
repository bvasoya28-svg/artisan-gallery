package com.artisan.gallery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String address;
    private String fullName;
    private String profilePicture;
    private boolean sellerTermsAccepted = false;
    @jakarta.persistence.Column(name = "lifetime_upload_count", nullable = false, columnDefinition = "int default 0")
    private int lifetimeUploadCount = 0;

    public User() {}

    public User(String username, String email, String password, String address, String fullName, String profilePicture, boolean sellerTermsAccepted) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.sellerTermsAccepted = sellerTermsAccepted;
        this.lifetimeUploadCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getProfilePicture() { 
        String img = (profilePicture == null || profilePicture.isEmpty() || profilePicture.equals("default-profile.png") || profilePicture.equals("v1.jpg")) 
            ? "https://res.cloudinary.com/dpt2wn9lh/image/upload/v1715851234/default-avatar_rc9v7x.png"
            : profilePicture;
            
        if (img.startsWith("http")) return img;
        
        // Strip extension and folder names for Cloudinary mapping
        String publicId = img.contains(".") ? img.substring(0, img.lastIndexOf('.')) : img;
        if (publicId.startsWith("/")) publicId = publicId.substring(1);
        if (publicId.startsWith("images/")) publicId = publicId.substring(7);
        
        return "https://res.cloudinary.com/dpt2wn9lh/image/upload/" + publicId;
    }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public boolean isSellerTermsAccepted() { return sellerTermsAccepted; }
    public void setSellerTermsAccepted(boolean sellerTermsAccepted) { this.sellerTermsAccepted = sellerTermsAccepted; }
    public int getLifetimeUploadCount() { return lifetimeUploadCount; }
    public void setLifetimeUploadCount(int lifetimeUploadCount) { this.lifetimeUploadCount = lifetimeUploadCount; }
}
