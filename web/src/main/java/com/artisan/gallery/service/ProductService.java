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

    @Autowired
    private org.springframework.beans.factory.ObjectProvider<ProductService> selfProvider;

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void initData() {
        Thread initThread = new Thread(() -> {
            try {
                Thread.sleep(10000); 
                System.out.println(">>> [STARTUP] Syncing 59 system items to restore images...");
                ProductService self = selfProvider.getIfAvailable();
                if (self != null) {
                    self.performSafeUpdate();
                }
                System.out.println(">>> [STARTUP] All items are now up to date!");
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Refresh failed: " + e.getMessage());
            }
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    @Transactional
    public void performSafeUpdate() {
        List<Product> systemProducts = repository.findByUploader("System");
        java.util.Map<String, Product> existingMap = systemProducts.stream()
                .collect(Collectors.toMap(Product::getName, p -> p, (p1, p2) -> p1));
        
        List<Product> toSave = new ArrayList<>();
        String baseUrl = "https://res.cloudinary.com/dpt2wn9lh/image/upload/";

        java.util.function.BiConsumer<String, String[]> updater = (name, data) -> {
            Product p = existingMap.getOrDefault(name, new Product());
            p.setName(name);
            p.setDescription(data[0]);
            p.setImageUrl(baseUrl + data[1]);
            p.setCategory(data[2]);
            p.setArtist(data[3]);
            p.setRating(Double.parseDouble(data[4]));
            p.setInStock(Boolean.parseBoolean(data[5]));
            if (p.getId() == null) {
                p.setUploader("System");
                p.setPrice((double)(1000 + new Random().nextInt(4000)));
                p.setReviewCount(10 + new Random().nextInt(90));
                p.setDeliveryTime("3-5 Days");
            }
            toSave.add(p);
        };

        // --- PAINTINGS (p1-p10) ---
        updater.accept("Fruit Still Life", new String[]{"Detailed oil painting of fresh harvest.", "p1", "Paintings", "Anita Sharma", "4.8", "false"});
        updater.accept("The Knight's Vow", new String[]{"Romantic classical masterpiece.", "p2", "Paintings", "Rajesh Kumar", "4.5", "true"});
        updater.accept("Coastal Village", new String[]{"Mediterranean town acrylic painting.", "p3", "Paintings", "Siddharth Verma", "4.9", "true"});
        updater.accept("Golden Moonbeam", new String[]{"Serene night sky moon painting.", "p4", "Paintings", "Neha Patel", "4.2", "true"});
        updater.accept("The Mermaid's Grace", new String[]{"Ethereal underwater portrait.", "p5", "Paintings", "Amitabh Gupta", "4.7", "false"});
        updater.accept("Flamenco Soul", new String[]{"Dynamic Spanish dancer painting.", "p6", "Paintings", "Priya Singh", "4.4", "true"});
        updater.accept("Royal Peacock", new String[]{"Textured acrylic peacock art.", "p7", "Paintings", "Vikram Malhotra", "4.6", "true"});
        updater.accept("The Forbidden Castle", new String[]{"Dark fantasy landscape.", "p8", "Paintings", "Deepika Iyer", "4.3", "true"});
        updater.accept("Voyage into Unknown", new String[]{"Ship sailing through a cave.", "p9", "Paintings", "Arjun Reddy", "4.7", "true"});
        updater.accept("Mystic Forest", new String[]{"Bioluminescent forest art.", "p10", "Paintings", "Ishani Bose", "4.6", "true"});

        // --- OTHERS (v1-v13) ---
        updater.accept("Leafy Tea Infuser", new String[]{"Silicon tea infuser.", "v1", "Others", "Karan Kapur", "4.7", "true"});
        updater.accept("English Breakfast Set", new String[]{"Ceramic breakfast set.", "v2", "Others", "Kaira Advani", "4.5", "true"});
        updater.accept("Blue Ceramic Teapot", new String[]{"Artisanal blue teapot.", "v3", "Others", "Suresh Raina", "4.8", "true"});
        updater.accept("Handcrafted Spice Rack", new String[]{"Teak wood spice rack.", "v4", "Others", "Manish Pandey", "4.3", "true"});
        updater.accept("Marble Mortar & Pestle", new String[]{"Solid marble grinding set.", "v5", "Others", "Hardik Pandya", "4.9", "true"});
        updater.accept("Bamboo Cutting Board", new String[]{"Sustainable cutting board.", "v6", "Others", "Rohit Sharma", "4.6", "true"});
        updater.accept("Copper Mixing Bowls", new String[]{"Hammered copper bowls.", "v7", "Others", "Virat Kohli", "4.4", "true"});
        updater.accept("Linen Apron", new String[]{"Natural linen apron.", "v8", "Others", "Shubman Gill", "4.2", "true"});
        updater.accept("Wooden Honey Dipper", new String[]{"Olive wood honey dipper.", "v9", "Others", "KL Rahul", "4.1", "true"});
        updater.accept("Ceramic Egg Carton", new String[]{"Vintage ceramic egg tray.", "v10", "Others", "Rishabh Pant", "4.5", "true"});
        updater.accept("Glass Herb Infuser", new String[]{"Glass bottle with strainer.", "v11", "Others", "Jasprit Bumrah", "4.3", "true"});
        updater.accept("Terracotta Bread Warmer", new String[]{"Terracotta warming stone.", "v12", "Others", "Mohammed Shami", "4.6", "true"});
        updater.accept("Woven Table Runner", new String[]{"Hand-woven cotton runner.", "v13", "Others", "Ravindra Jadeja", "4.4", "true"});

        // --- CRAFT (c1-c10 + v14-v19) ---
        updater.accept("Boho Macrame Wall Hanging", new String[]{"Handcrafted macrame art.", "c1", "Craft", "Ishaan Khatter", "4.8", "true"});
        updater.accept("Artisanal Scented Candle", new String[]{"Lavender soy wax candle.", "c2", "Craft", "Ananya Panday", "4.6", "true"});
        updater.accept("Woven Seagrass Basket", new String[]{"Versatile storage basket.", "c3", "Craft", "Sara Ali Khan", "4.5", "true"});
        updater.accept("Crystal Geode Bookends", new String[]{"Natural purple amethyst.", "c4", "Craft", "Janhvi Kapoor", "4.9", "true"});
        updater.accept("Vintage Brass Mirror", new String[]{"Hand-etched brass frame.", "c5", "Craft", "Kartik Aaryan", "4.4", "true"});
        updater.accept("Embroidered Silk Cushion", new String[]{"Silk cover with floral motifs.", "c6", "Craft", "Ayushmann Khurrana", "4.3", "true"});
        updater.accept("Modern Ceramic Vase", new String[]{"Minimalist matte white vase.", "c7", "Craft", "Ranbir Kapoor", "4.7", "true"});
        updater.accept("Hand-tufted Rug", new String[]{"Soft wool geometric rug.", "c8", "Craft", "Alia Bhatt", "4.6", "true"});
        updater.accept("Sculptural Iron Lamp", new String[]{"Industrial iron base lamp.", "c9", "Craft", "Ranveer Singh", "4.5", "true"});
        updater.accept("Botanical Framed Print", new String[]{"Vintage botanical print.", "c10", "Craft", "Deepika Padukone", "4.2", "true"});
        updater.accept("Terrarium Kit", new String[]{"Complete mini garden kit.", "v14", "Others", "Vicky Kaushal", "4.8", "true"});
        updater.accept("Driftwood Wall Clock", new String[]{"Rustic driftwood clock.", "v15", "Others", "Katrina Kaif", "4.4", "true"});
        updater.accept("Hand-painted Tile Coasters", new String[]{"Set of 4 ceramic coasters.", "v16", "Others", "Shahid Kapoor", "4.3", "true"});
        updater.accept("Macrame Plant Hanger", new String[]{"Hand-knotted plant hanger.", "v17", "Others", "Shraddha Kapoor", "4.7", "true"});
        updater.accept("Wooden Wall Shelf", new String[]{"Floating oak wall shelf.", "v18", "Others", "Varun Dhawan", "4.5", "true"});
        updater.accept("Decorative Lantern", new String[]{"Vintage metal lantern.", "v19", "Others", "Kriti Sanon", "4.6", "true"});

        // --- CROCHET (cr1-cr10) ---
        updater.accept("Silver Moonstone Ring", new String[]{"Sterling silver moonstone ring.", "cr1", "Crochet", "Sonam Kapoor", "4.9", "true"});
        updater.accept("Gold Filigree Earrings", new String[]{"18k gold-plated earrings.", "cr2", "Crochet", "Kareena Kapoor", "4.7", "true"});
        updater.accept("Pearl Pendant Necklace", new String[]{"Classic freshwater pearl.", "cr3", "Crochet", "Priyanka Chopra", "4.8", "true"});
        updater.accept("Beaded Boho Bracelet", new String[]{"Multi-strand bead bracelet.", "cr4", "Crochet", "Shraddha Kapoor", "4.4", "true"});
        updater.accept("Hammered Copper Cuff", new String[]{"Pure hammered copper cuff.", "cr5", "Crochet", "Tiger Shroff", "4.3", "true"});
        updater.accept("Emerald Stud Earrings", new String[]{"Green emerald silver studs.", "cr6", "Crochet", "Varun Dhawan", "4.6", "true"});
        updater.accept("Turquoise Statement Necklace", new String[]{"Chunky turquoise necklace.", "cr7", "Crochet", "Sidharth Malhotra", "4.5", "true"});
        updater.accept("Amethyst Drop Earrings", new String[]{"Teardrop amethyst wires.", "cr8", "Crochet", "Kiara Advani", "4.7", "true"});
        updater.accept("Rose Gold Initial Ring", new String[]{"Dainty initial ring.", "cr9", "Crochet", "Kriti Sanon", "4.2", "true"});
        updater.accept("Leather Braided Bracelet", new String[]{"Leather anchor bracelet.", "cr10", "Crochet", "Hrithik Roshan", "4.5", "true"});

        // --- POTTERY (po1-po10) ---
        updater.accept("Hand-thrown Coffee Mug", new String[]{"Speckled wheel-thrown mug.", "po1", "Pottery", "Rajkumar Rao", "4.7", "true"});
        updater.accept("Speckled Serving Bowl", new String[]{"Rustic salad bowl.", "po2", "Pottery", "Ayushmann Khurrana", "4.6", "true"});
        updater.accept("Mini Succulent Planters", new String[]{"Set of 3 tiny ceramic pots.", "po3", "Pottery", "Pankaj Tripathi", "4.5", "true"});
        updater.accept("Ribbed Clay Vase", new String[]{"Tall ribbed clay vase.", "po4", "Pottery", "Nawazuddin Siddiqui", "4.4", "true"});
        updater.accept("Glazed Incense Holder", new String[]{"Celadon green incense holder.", "po5", "Pottery", "Manoj Bajpayee", "4.8", "true"});
        updater.accept("Geometric Fruit Bowl", new String[]{"Modern faceted design.", "po6", "Pottery", "Vijay Varma", "4.3", "true"});
        updater.accept("Blue Ceramic Teacup", new String[]{"Japanese style teacup.", "po7", "Pottery", "Siddhant Chaturvedi", "4.5", "true"});
        updater.accept("Textured Wall Planter", new String[]{"Flat-backed clay planter.", "po8", "Pottery", "Vikrant Massey", "4.2", "true"});
        updater.accept("Rustic Pitcher", new String[]{"Hand-formed water pitcher.", "po9", "Pottery", "Jaideep Ahlawat", "4.6", "true"});
        updater.accept("Hand-painted Plate Set", new String[]{"Set of 4 dessert plates.", "po10", "Pottery", "Pratik Gandhi", "4.4", "true"});

        repository.saveAll(toSave);
        repository.flush();
    }

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
