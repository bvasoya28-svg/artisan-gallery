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
                System.out.println(">>> [STARTUP] Restoring images with EXACT mapping (p, po, cr, c, v)...");
                performSafeUpdate();
                System.out.println(">>> [STARTUP] All 59 items restored with full stories!");
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
        update(toUpdate, existing, "Fruit Still Life", "This stunning oil painting captures the vibrant essence of fresh harvest, featuring a meticulously detailed arrangement of ripe apples, velvet-skinned grapes, and a rustic wooden bowl. Ideal for a sophisticated kitchen or dining area.", baseUrl + "p1.jpg", "Paintings", "Anita Sharma", 4.8);
        update(toUpdate, existing, "The Knight's Vow", "A breathtaking romantic classical masterpiece depicting a solemn knight kneeling before a radiant princess in a medieval castle hall. A powerful focal point for any grand living room.", baseUrl + "p2.jpg", "Paintings", "Rajesh Kumar", 4.5);
        update(toUpdate, existing, "Coastal Village", "Immerse yourself in the charm of a sun-drenched Mediterranean town with this vibrant acrylic painting. Cascading houses overlook a sparkling turquoise sea.", baseUrl + "p3.jpg", "Paintings", "Siddharth Verma", 4.9);
        update(toUpdate, existing, "Golden Moonbeam", "A serene night sky featuring a large golden moon reflected on a calm sea. Perfect for a bedroom or meditation space.", baseUrl + "p4.jpg", "Paintings", "Neha Patel", 4.2);
        update(toUpdate, existing, "The Mermaid's Grace", "An ethereal underwater portrait of a mermaid in deep blue waters, exploring the mystery of the deep ocean.", baseUrl + "p5.jpg", "Paintings", "Amitabh Gupta", 4.7);
        update(toUpdate, existing, "Flamenco Soul", "A dynamic painting of a dancer in a vibrant red dress, capturing the raw passion and energy of Spanish dance.", baseUrl + "p6.jpg", "Paintings", "Priya Singh", 4.4);
        update(toUpdate, existing, "Royal Peacock", "A textured acrylic painting of a peacock with a stunning tail highlighted with metallic gold and emerald green.", baseUrl + "p7.jpg", "Paintings", "Vikram Malhotra", 4.6);
        update(toUpdate, existing, "The Forbidden Castle", "An atmospheric dark fantasy landscape featuring a mysterious gothic castle perched on a jagged cliff.", baseUrl + "p8.jpg", "Paintings", "Deepika Iyer", 4.3);
        update(toUpdate, existing, "Voyage into Unknown", "A tall ship sailing through a rocky cave opening, signifying the start of a legendary and brave journey.", baseUrl + "p9.jpg", "Paintings", "Arjun Reddy", 4.7);
        update(toUpdate, existing, "Mystic Forest", "A high-fantasy landscape capturing a glowing, bioluminescent forest at twilight with enchanted trees.", baseUrl + "p10.jpg", "Paintings", "Ishani Bose", 4.6);

        // --- KITCHEN (v1-v13) ---
        update(toUpdate, existing, "Leafy Tea Infuser", "Brew the perfect cup of loose-leaf tea with this charming sprout-shaped infuser. Made from food-grade silicone, it's a delightful gift for nature lovers.", baseUrl + "v1.jpg", "Kitchen", "Karan Kapur", 4.7);
        update(toUpdate, existing, "English Breakfast Set", "A complete ceramic breakfast set featuring mugs, plates, and a teapot with a classic ivory glaze. Hand-thrown and fired for durability.", baseUrl + "v2.jpg", "Kitchen", "Kaira Advani", 4.5);
        update(toUpdate, existing, "Blue Ceramic Teapot", "Artisanal teapot with a deep cobalt blue glaze and subtle earthy speckles. Ergonomic handle and drip-free spout for the perfect pour.", baseUrl + "v3.jpg", "Kitchen", "Suresh Raina", 4.8);
        update(toUpdate, existing, "Handcrafted Spice Rack", "Three-tier spice rack made from reclaimed teak wood. Includes 12 glass jars with airtight wooden lids to preserve freshness.", baseUrl + "v4.jpg", "Kitchen", "Manish Pandey", 4.3);
        update(toUpdate, existing, "Marble Mortar & Pestle", "Heavy-duty solid marble set for grinding herbs like a pro. Polished exterior and rough interior for perfect crushing.", baseUrl + "v5.jpg", "Kitchen", "Hardik Pandya", 4.9);
        update(toUpdate, existing, "Bamboo Cutting Board", "Sustainable and knife-friendly board with a built-in juice groove. Large surface ideal for chopping or serving cheese.", baseUrl + "v6.jpg", "Kitchen", "Rohit Sharma", 4.6);
        update(toUpdate, existing, "Copper Mixing Bowls", "Set of three stainless steel bowls with hammered copper exterior. Functional for baking and beautiful for serving.", baseUrl + "v7.jpg", "Kitchen", "Virat Kohli", 4.4);
        update(toUpdate, existing, "Linen Apron", "Stylish and durable cross-back apron made from 100% natural linen. Features two deep pockets and a comfortable fit.", baseUrl + "v8.jpg", "Kitchen", "Shubman Gill", 4.2);
        update(toUpdate, existing, "Wooden Honey Dipper", "Hand-carved olive wood dipper for mess-free drizzling. Deep grooves designed to hold honey until you're ready.", baseUrl + "v9.jpg", "Kitchen", "KL Rahul", 4.1);
        update(toUpdate, existing, "Ceramic Egg Carton", "Reusable ceramic tray for half a dozen eggs. Adds a charming vintage touch to your kitchen organization.", baseUrl + "v10.jpg", "Kitchen", "Rishabh Pant", 4.5);
        update(toUpdate, existing, "Glass Herb Infuser", "Elegant glass bottle with built-in strainer for infusing oils with fresh herbs and garlic. A must-have for home cooks.", baseUrl + "v11.jpg", "Kitchen", "Jasprit Bumrah", 4.3);
        update(toUpdate, existing, "Terracotta Bread Warmer", "Engraved terracotta stone to keep bread warm throughout dinner. Simply heat in the oven and place in your basket.", baseUrl + "v12.jpg", "Kitchen", "Mohammed Shami", 4.6);
        update(toUpdate, existing, "Woven Table Runner", "Hand-woven cotton runner with intricate patterns and fringed edges. Adds a touch of bohemian style to any table.", baseUrl + "v13.jpg", "Kitchen", "Ravindra Jadeja", 4.4);

        // --- HOME DECOR (c1-c10 + v14-v19) ---
        update(toUpdate, existing, "Boho Macrame Wall Hanging", "Intricate macrame wall art handcrafted with 100% natural cotton. Adds a cozy, bohemian vibe to your gallery wall.", baseUrl + "c1.jpg", "Home Decor", "Ishaan Khatter", 4.8);
        update(toUpdate, existing, "Artisanal Scented Candle", "Hand-poured soy wax candle in a reusable ceramic jar. Lavender and sandalwood scent with a crackling wood wick.", baseUrl + "c2.jpg", "Home Decor", "Ananya Panday", 4.6);
        update(toUpdate, existing, "Woven Seagrass Basket", "Versatile storage solution that doubles as a stylish planter. Hand-woven by skilled artisans with reinforced handles.", baseUrl + "c3.jpg", "Home Decor", "Sara Ali Khan", 4.5);
        update(toUpdate, existing, "Crystal Geode Bookends", "Natural luxury for your bookshelf. Heavy amethyst geode halves with brilliant purple crystals that sparkle in the light.", baseUrl + "c4.jpg", "Home Decor", "Janhvi Kapoor", 4.9);
        update(toUpdate, existing, "Vintage Brass Mirror", "Classic round mirror with a hand-etched brass frame. Timeless design that adds depth and character to any room.", baseUrl + "c5.jpg", "Home Decor", "Kartik Aaryan", 4.4);
        update(toUpdate, existing, "Embroidered Silk Cushion", "Luxurious silk cushion cover with hand-embroidered floral motifs. Adds elegance and a pop of color to your sofa.", baseUrl + "c6.jpg", "Home Decor", "Ayushmann Khurrana", 4.3);
        update(toUpdate, existing, "Modern Ceramic Vase", "Minimalist matte white vase with a unique asymmetrical shape. Ideal for dried pampas grass or fresh blooms.", baseUrl + "c7.jpg", "Home Decor", "Ranbir Kapoor", 4.7);
        update(toUpdate, existing, "Hand-tufted Rug", "Soft and durable wool rug with a bold geometric pattern. Hand-tufted for warmth and comfort underfoot.", baseUrl + "c8.jpg", "Home Decor", "Alia Bhatt", 4.6);
        update(toUpdate, existing, "Sculptural Iron Lamp", "Industrial-style lamp with a hand-forged iron base and warm Edison bulb. Creates a sophisticated atmosphere.", baseUrl + "c9.jpg", "Home Decor", "Ranveer Singh", 4.5);
        update(toUpdate, existing, "Botanical Framed Print", "Vintage botanical illustration in a slim oak frame. Perfect for creating a nature-inspired gallery wall.", baseUrl + "c10.jpg", "Home Decor", "Deepika Padukone", 4.2);
        update(toUpdate, existing, "Terrarium Kit", "Create your own miniature garden. Includes geometric glass container, soil, and moss. Just add succulents!", baseUrl + "v14.jpg", "Home Decor", "Vicky Kaushal", 4.8);
        update(toUpdate, existing, "Driftwood Wall Clock", "Rustic wall clock crafted from natural driftwood pieces. Silent movement and minimalist hands.", baseUrl + "v15.jpg", "Home Decor", "Katrina Kaif", 4.4);
        update(toUpdate, existing, "Hand-painted Tile Coasters", "Set of four ceramic coasters with traditional motifs. Cork-backed to protect furniture from heat.", baseUrl + "v16.jpg", "Home Decor", "Shahid Kapoor", 4.3);
        update(toUpdate, existing, "Macrame Plant Hanger", "Hand-knotted cotton rope hanger for your favorite indoor plants. Sturdy and elegant bohemian design.", baseUrl + "v17.jpg", "Home Decor", "Shraddha Kapoor", 4.7);
        update(toUpdate, existing, "Wooden Wall Shelf", "Minimalist floating shelf made from solid oak. Perfect for displaying small decor items or books.", baseUrl + "v18.jpg", "Home Decor", "Varun Dhawan", 4.5);
        update(toUpdate, existing, "Decorative Lantern", "Vintage-inspired metal lantern with intricate cut-outs. Creates beautiful shadow patterns when lit.", baseUrl + "v19.jpg", "Home Decor", "Kriti Sanon", 4.6);

        // --- JEWELRY / CROCHET (cr1-cr10) ---
        update(toUpdate, existing, "Silver Moonstone Ring", "Delicate sterling silver ring with an iridescent moonstone. Glows with a mysterious blue light. A perfect statement piece.", baseUrl + "cr1.jpg", "Jewelry", "Sonam Kapoor", 4.9);
        update(toUpdate, existing, "Gold Filigree Earrings", "Intricate 18k gold-plated earrings inspired by traditional motifs. Lightweight and regal for any outfit.", baseUrl + "cr2.jpg", "Jewelry", "Kareena Kapoor", 4.7);
        update(toUpdate, existing, "Pearl Pendant Necklace", "Classic necklace with a genuine freshwater pearl on a fine 14k gold chain. Symbolizes purity and grace.", baseUrl + "cr3.jpg", "Jewelry", "Priyanka Chopra", 4.8);
        update(toUpdate, existing, "Beaded Boho Bracelet", "Colorful multi-strand bracelet made with tiny glass seed beads. Features a secure magnetic clasp for easy wear.", baseUrl + "cr4.jpg", "Jewelry", "Shraddha Kapoor", 4.4);
        update(toUpdate, existing, "Hammered Copper Cuff", "Bold and rustic cuff made from pure hammered copper. Develops a beautiful natural patina over time.", baseUrl + "cr5.jpg", "Jewelry", "Tiger Shroff", 4.3);
        update(toUpdate, existing, "Emerald Stud Earrings", "Vibrant green emeralds set in minimalist 925 sterling silver studs. A touch of luxury for everyday wear.", baseUrl + "cr6.jpg", "Jewelry", "Varun Dhawan", 4.6);
        update(toUpdate, existing, "Turquoise Statement Necklace", "Chunky necklace with large turquoise stones and silver accents. Brings a Southwestern flair to any look.", baseUrl + "cr7.jpg", "Jewelry", "Sidharth Malhotra", 4.5);
        update(toUpdate, existing, "Amethyst Drop Earrings", "Teardrop amethyst stones on delicate silver wires. Said to promote calm and clarity for the wearer.", baseUrl + "cr8.jpg", "Jewelry", "Kiara Advani", 4.7);
        update(toUpdate, existing, "Rose Gold Initial Ring", "Personalized ring with a dainty initial on a thin rose gold band. A thoughtful and stylish gift.", baseUrl + "cr9.jpg", "Jewelry", "Kriti Sanon", 4.2);
        update(toUpdate, existing, "Leather Braided Bracelet", "Rugged masculine bracelet made from high-quality leather with a stainless steel anchor clasp.", baseUrl + "cr10.jpg", "Jewelry", "Hrithik Roshan", 4.5);

        // --- POTTERY (po1-po10) ---
        update(toUpdate, existing, "Hand-thrown Coffee Mug", "Large earthy mug hand-thrown on a potter's wheel. Unique speckled glaze and wide handle for a comfy grip.", baseUrl + "po1.jpg", "Pottery", "Rajkumar Rao", 4.7);
        update(toUpdate, existing, "Speckled Serving Bowl", "Large shallow bowl perfect for salads. Cream glaze with dark iron spots for a rustic-modern feel.", baseUrl + "po2.jpg", "Pottery", "Ayushmann Khurrana", 4.6);
        update(toUpdate, existing, "Mini Succulent Planters", "Set of three tiny ceramic pots with drainage holes. Ideal for small succulents on a sunny windowsill.", baseUrl + "po3.jpg", "Pottery", "Pankaj Tripathi", 4.5);
        update(toUpdate, existing, "Ribbed Clay Vase", "Tall slender vase with a distinctive ribbed texture. Natural terracotta color complements any flowers.", baseUrl + "po4.jpg", "Pottery", "Nawazuddin Siddiqui", 4.4);
        update(toUpdate, existing, "Glazed Incense Holder", "Minimalist teardrop shape that catches all ash. Hand-dipped in a calming celadon green glaze.", baseUrl + "po5.jpg", "Pottery", "Manoj Bajpayee", 4.8);
        update(toUpdate, existing, "Geometric Fruit Bowl", "Striking faceted design that adds a modern architectural touch to your kitchen island.", baseUrl + "po6.jpg", "Pottery", "Vijay Varma", 4.3);
        update(toUpdate, existing, "Blue Ceramic Teacup", "Elegant teacup inspired by Japanese tea ceremonies. Smooth glaze feels wonderful in your hands.", baseUrl + "po7.jpg", "Pottery", "Siddhant Chaturvedi", 4.5);
        update(toUpdate, existing, "Textured Wall Planter", "Save space with this flat-backed ceramic planter. Rough sandy texture contrasts beautifully with green leaves.", baseUrl + "po8.jpg", "Pottery", "Vikrant Massey", 4.2);
        update(toUpdate, existing, "Rustic Pitcher", "Hand-formed pitcher with wide spout and sturdy handle. Perfect for serving cold drinks in summer.", baseUrl + "po9.jpg", "Pottery", "Jaideep Ahlawat", 4.6);
        update(toUpdate, existing, "Hand-painted Plate Set", "Set of four dessert plates with different botanical motifs. A delightful way to end any meal.", baseUrl + "po10.jpg", "Pottery", "Pratik Gandhi", 4.4);
        
        saveBatch(toUpdate);
    }

    private void update(List<Product> toSave, List<Product> existing, String name, String desc, String img, String cat, String art, Double rate) {
        Product p = existing.stream()
                .filter(ext -> ext.getName().equals(name))
                .findFirst()
                .orElse(new Product());
        
        p.setName(name);
        p.setDescription(desc); // Restore the full story
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
