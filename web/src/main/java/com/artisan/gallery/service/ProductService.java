package com.artisan.gallery.service;

import com.artisan.gallery.model.Product;
import com.artisan.gallery.repository.ProductRepository;
import com.artisan.gallery.repository.CartItemRepository;
import com.artisan.gallery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void initData() {
        Thread initThread = new Thread(() -> {
            try {
                Thread.sleep(5000); 
                System.out.println(">>> [STARTUP] Restoring LIVE Cloudinary images...");
                performSafeUpdate();
                System.out.println(">>> [STARTUP] Images restored!");
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Restore failed: " + e.getMessage());
            }
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    public void performSafeUpdate() {
        List<Product> existing = repository.findByUploader("System");
        List<Product> toUpdate = new ArrayList<>();
        
        String baseUrl = "https://res.cloudinary.com/dph6v9re2/image/upload/v1735111111/artisan/";

        // --- PAINTINGS (p1-p10) ---
        update(toUpdate, existing, "Fruit Still Life", "...", baseUrl + "p1.jpg", "Paintings", "Anita Sharma", 4.8);
        update(toUpdate, existing, "The Knight's Vow", "...", baseUrl + "p2.jpg", "Paintings", "Rajesh Kumar", 4.5);
        update(toUpdate, existing, "Coastal Village", "...", baseUrl + "p3.jpg", "Paintings", "Siddharth Verma", 4.9);
        update(toUpdate, existing, "Golden Moonbeam", "...", baseUrl + "p4.jpg", "Paintings", "Neha Patel", 4.2);
        update(toUpdate, existing, "The Mermaid's Grace", "...", baseUrl + "p5.jpg", "Paintings", "Amitabh Gupta", 4.7);
        update(toUpdate, existing, "Flamenco Soul", "...", baseUrl + "p6.jpg", "Paintings", "Priya Singh", 4.4);
        update(toUpdate, existing, "Royal Peacock", "...", baseUrl + "p7.jpg", "Paintings", "Vikram Malhotra", 4.6);
        update(toUpdate, existing, "The Forbidden Castle", "...", baseUrl + "p8.jpg", "Paintings", "Deepika Iyer", 4.3);
        update(toUpdate, existing, "Voyage into Unknown", "...", baseUrl + "p9.jpg", "Paintings", "Arjun Reddy", 4.7);
        update(toUpdate, existing, "Mystic Forest", "...", baseUrl + "p10.jpg", "Paintings", "Ishani Bose", 4.6);

        // --- KITCHEN (k1-k13) ---
        update(toUpdate, existing, "Leafy Tea Infuser", "...", baseUrl + "k1.jpg", "Kitchen", "Karan Kapur", 4.7);
        update(toUpdate, existing, "English Breakfast Set", "...", baseUrl + "k2.jpg", "Kitchen", "Kaira Advani", 4.5);
        update(toUpdate, existing, "Blue Ceramic Teapot", "...", baseUrl + "k3.jpg", "Kitchen", "Suresh Raina", 4.8);
        update(toUpdate, existing, "Handcrafted Spice Rack", "...", baseUrl + "k4.jpg", "Kitchen", "Manish Pandey", 4.3);
        update(toUpdate, existing, "Marble Mortar & Pestle", "...", baseUrl + "k5.jpg", "Kitchen", "Hardik Pandya", 4.9);
        update(toUpdate, existing, "Bamboo Cutting Board", "...", baseUrl + "k6.jpg", "Kitchen", "Rohit Sharma", 4.6);
        update(toUpdate, existing, "Copper Mixing Bowls", "...", baseUrl + "k7.jpg", "Kitchen", "Virat Kohli", 4.4);
        update(toUpdate, existing, "Linen Apron", "...", baseUrl + "k8.jpg", "Kitchen", "Shubman Gill", 4.2);
        update(toUpdate, existing, "Wooden Honey Dipper", "...", baseUrl + "k9.jpg", "Kitchen", "KL Rahul", 4.1);
        update(toUpdate, existing, "Ceramic Egg Carton", "...", baseUrl + "k10.jpg", "Kitchen", "Rishabh Pant", 4.5);
        update(toUpdate, existing, "Glass Herb Infuser", "...", baseUrl + "k11.jpg", "Kitchen", "Jasprit Bumrah", 4.3);
        update(toUpdate, existing, "Terracotta Bread Warmer", "...", baseUrl + "k12.jpg", "Kitchen", "Mohammed Shami", 4.6);
        update(toUpdate, existing, "Woven Table Runner", "...", baseUrl + "k13.jpg", "Kitchen", "Ravindra Jadeja", 4.4);

        // --- HOME DECOR (d1-d13) ---
        update(toUpdate, existing, "Boho Macrame Wall Hanging", "...", baseUrl + "d1.jpg", "Home Decor", "Ishaan Khatter", 4.8);
        update(toUpdate, existing, "Artisanal Scented Candle", "...", baseUrl + "d2.jpg", "Home Decor", "Ananya Panday", 4.6);
        update(toUpdate, existing, "Woven Seagrass Basket", "...", baseUrl + "d3.jpg", "Home Decor", "Sara Ali Khan", 4.5);
        update(toUpdate, existing, "Crystal Geode Bookends", "...", baseUrl + "d4.jpg", "Home Decor", "Janhvi Kapoor", 4.9);
        update(toUpdate, existing, "Vintage Brass Mirror", "...", baseUrl + "d5.jpg", "Home Decor", "Kartik Aaryan", 4.4);
        update(toUpdate, existing, "Embroidered Silk Cushion", "...", baseUrl + "d6.jpg", "Home Decor", "Ayushmann Khurrana", 4.3);
        update(toUpdate, existing, "Modern Ceramic Vase", "...", baseUrl + "d7.jpg", "Home Decor", "Ranbir Kapoor", 4.7);
        update(toUpdate, existing, "Hand-tufted Rug", "...", baseUrl + "d8.jpg", "Home Decor", "Alia Bhatt", 4.6);
        update(toUpdate, existing, "Sculptural Iron Lamp", "...", baseUrl + "d9.jpg", "Home Decor", "Ranveer Singh", 4.5);
        update(toUpdate, existing, "Botanical Framed Print", "...", baseUrl + "d10.jpg", "Home Decor", "Deepika Padukone", 4.2);
        update(toUpdate, existing, "Terrarium Kit", "...", baseUrl + "d11.jpg", "Home Decor", "Vicky Kaushal", 4.8);
        update(toUpdate, existing, "Driftwood Wall Clock", "...", baseUrl + "d12.jpg", "Home Decor", "Katrina Kaif", 4.4);
        update(toUpdate, existing, "Hand-painted Tile Coasters", "...", baseUrl + "d13.jpg", "Home Decor", "Shahid Kapoor", 4.3);

        // --- JEWELRY (j1-j13) ---
        update(toUpdate, existing, "Silver Moonstone Ring", "...", baseUrl + "j1.jpg", "Jewelry", "Sonam Kapoor", 4.9);
        update(toUpdate, existing, "Gold Filigree Earrings", "...", baseUrl + "j2.jpg", "Jewelry", "Kareena Kapoor", 4.7);
        update(toUpdate, existing, "Pearl Pendant Necklace", "...", baseUrl + "j3.jpg", "Jewelry", "Priyanka Chopra", 4.8);
        update(toUpdate, existing, "Beaded Boho Bracelet", "...", baseUrl + "j4.jpg", "Jewelry", "Shraddha Kapoor", 4.4);
        update(toUpdate, existing, "Hammered Copper Cuff", "...", baseUrl + "j5.jpg", "Jewelry", "Tiger Shroff", 4.3);
        update(toUpdate, existing, "Emerald Stud Earrings", "...", baseUrl + "j6.jpg", "Jewelry", "Varun Dhawan", 4.6);
        update(toUpdate, existing, "Turquoise Statement Necklace", "...", baseUrl + "j7.jpg", "Jewelry", "Sidharth Malhotra", 4.5);
        update(toUpdate, existing, "Amethyst Drop Earrings", "...", baseUrl + "j8.jpg", "Jewelry", "Kiara Advani", 4.7);
        update(toUpdate, existing, "Rose Gold Initial Ring", "...", baseUrl + "j9.jpg", "Jewelry", "Kriti Sanon", 4.2);
        update(toUpdate, existing, "Leather Braided Bracelet", "...", baseUrl + "j10.jpg", "Jewelry", "Hrithik Roshan", 4.5);
        update(toUpdate, existing, "Labradorite Hoop Earrings", "...", baseUrl + "j11.jpg", "Jewelry", "Tara Sutaria", 4.4);
        update(toUpdate, existing, "Raw Quartz Crystal Point", "...", baseUrl + "j12.jpg", "Jewelry", "Disha Patani", 4.6);
        update(toUpdate, existing, "Minimalist Bar Necklace", "...", baseUrl + "j13.jpg", "Jewelry", "Arjun Kapoor", 4.3);

        // --- POTTERY (po1-po10) ---
        update(toUpdate, existing, "Hand-thrown Coffee Mug", "...", baseUrl + "po1.jpg", "Pottery", "Rajkumar Rao", 4.7);
        update(toUpdate, existing, "Speckled Serving Bowl", "...", baseUrl + "po2.jpg", "Pottery", "Ayushmann Khurrana", 4.6);
        update(toUpdate, existing, "Mini Succulent Planters", "...", baseUrl + "po3.jpg", "Pottery", "Pankaj Tripathi", 4.5);
        update(toUpdate, existing, "Ribbed Clay Vase", "...", baseUrl + "po4.jpg", "Pottery", "Nawazuddin Siddiqui", 4.4);
        update(toUpdate, existing, "Glazed Incense Holder", "...", baseUrl + "po5.jpg", "Pottery", "Manoj Bajpayee", 4.8);
        update(toUpdate, existing, "Geometric Fruit Bowl", "...", baseUrl + "po6.jpg", "Pottery", "Vijay Varma", 4.3);
        update(toUpdate, existing, "Blue Ceramic Teacup", "...", baseUrl + "po7.jpg", "Pottery", "Siddhant Chaturvedi", 4.5);
        update(toUpdate, existing, "Textured Wall Planter", "...", baseUrl + "po8.jpg", "Pottery", "Vikrant Massey", 4.2);
        update(toUpdate, existing, "Rustic Pitcher", "...", baseUrl + "po9.jpg", "Pottery", "Jaideep Ahlawat", 4.6);
        update(toUpdate, existing, "Hand-painted Plate Set", "...", baseUrl + "po10.jpg", "Pottery", "Pratik Gandhi", 4.4);
        
        saveBatch(toUpdate);
    }

    private void update(List<Product> toSave, List<Product> existing, String name, String desc, String img, String cat, String art, Double rate) {
        Product p = existing.stream()
                .filter(ext -> ext.getName().equals(name))
                .findFirst()
                .orElse(new Product());
        
        p.setName(name);
        // Only update description if it's currently too short (to preserve your stories)
        if (p.getDescription() == null || p.getDescription().length() < 10) {
            p.setDescription(desc);
        }
        p.setImageUrl(img);
        p.setCategory(cat);
        p.setArtist(art);
        p.setRating(rate);
        if (p.getPrice() == null) p.setPrice((double)(1000 + new Random().nextInt(4000)));
        p.setUploader("System");
        p.setInStock(true);
        toSave.add(p);
    }

    @Transactional
    protected void saveBatch(List<Product> products) {
        repository.saveAll(products);
    }

    // --- Controller Support Methods ---
    public List<Product> getAllProducts() { return repository.findAll(); }
    public Product getProductById(Long id) { return repository.findById(id).orElse(null); }
    public List<Product> getProductsByCategory(String category) { return repository.findByCategory(category); }
    public List<Product> searchProducts(String query) { return repository.findByNameContainingIgnoreCase(query); }
    public Product saveProduct(Product product) { return repository.save(product); }
    public List<Product> getSystemProducts() { return repository.findByUploader("System"); }
    public List<Product> getUserItems(String email) { return repository.findByUploader(email); }
    public List<Product> getSharedCreations(String email) { return repository.findByUploaderNotAndUploaderNot("System", email); }
    public List<Product> getSuggestions(String category, Long excludeId) {
        return repository.findByCategory(category).stream()
                .filter(p -> !p.getId().equals(excludeId))
                .limit(4)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long id) {
        reviewRepository.deleteByProductId(id);
        cartItemRepository.deleteByProductIds(java.util.Collections.singletonList(id));
        repository.deleteById(id);
    }
}
