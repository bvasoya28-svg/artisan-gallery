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
                System.out.println(">>> [STARTUP] Starting FULL catalog update...");
                performSafeUpdate();
                System.out.println(">>> [STARTUP] Catalog update complete!");
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Update failed: " + e.getMessage());
            }
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    public void performSafeUpdate() {
        List<Product> existing = repository.findByUploader("System");
        List<Product> toUpdate = new ArrayList<>();
        
        // --- PAINTINGS (p1-p10) ---
        update(toUpdate, existing, "Fruit Still Life", "This stunning oil painting captures the vibrant essence of fresh harvest, featuring a meticulously detailed arrangement of ripe apples, velvet-skinned grapes, and a rustic wooden bowl. Ideal for a sophisticated kitchen or dining area.", "p1.jpg", "Paintings", "Anita Sharma", 4.8);
        update(toUpdate, existing, "The Knight's Vow", "A breathtaking romantic classical masterpiece depicting a solemn knight kneeling before a radiant princess in a medieval castle hall. A powerful focal point for any grand living room.", "p2.jpg", "Paintings", "Rajesh Kumar", 4.5);
        update(toUpdate, existing, "Coastal Village", "Immerse yourself in the charm of a sun-drenched Mediterranean town with this vibrant acrylic painting. Cascading houses overlook a sparkling turquoise sea.", "p3.jpg", "Paintings", "Siddharth Verma", 4.9);
        update(toUpdate, existing, "Golden Moonbeam", "A serene night sky featuring a large golden moon reflected on a calm sea. Perfect for a bedroom or meditation space.", "p4.jpg", "Paintings", "Neha Patel", 4.2);
        update(toUpdate, existing, "The Mermaid's Grace", "An ethereal underwater portrait of a mermaid in deep blue waters, exploring the mystery of the deep ocean.", "p5.jpg", "Paintings", "Amitabh Gupta", 4.7);
        update(toUpdate, existing, "Flamenco Soul", "A dynamic painting of a dancer in a vibrant red dress, capturing the raw passion and energy of Spanish dance.", "p6.jpg", "Paintings", "Priya Singh", 4.4);
        update(toUpdate, existing, "Royal Peacock", "A textured acrylic painting of a peacock with a stunning tail highlighted with metallic gold and emerald green.", "p7.jpg", "Paintings", "Vikram Malhotra", 4.6);
        update(toUpdate, existing, "The Forbidden Castle", "An atmospheric dark fantasy landscape featuring a mysterious gothic castle perched on a jagged cliff.", "p8.jpg", "Paintings", "Deepika Iyer", 4.3);
        update(toUpdate, existing, "Voyage into Unknown", "A tall ship sailing through a rocky cave opening, signifying the start of a legendary and brave journey.", "p9.jpg", "Paintings", "Arjun Reddy", 4.7);
        update(toUpdate, existing, "Mystic Forest", "A high-fantasy landscape capturing a glowing, bioluminescent forest at twilight with enchanted trees.", "p10.jpg", "Paintings", "Ishani Bose", 4.6);

        // --- KITCHEN (k1-k13) ---
        update(toUpdate, existing, "Leafy Tea Infuser", "Brew the perfect cup of loose-leaf tea with this charming and practical infuser. The handle is shaped like a delicate green sprout, making it look like a plant is growing out of your mug. Made from food-grade, heat-resistant silicone, it is easy to clean and fits most standard mugs. It's a delightful gift for nature lovers and tea enthusiasts alike.", "k1.jpg", "Kitchen", "Karan Kapur", 4.7);
        update(toUpdate, existing, "English Breakfast Set", "A complete ceramic breakfast set featuring two mugs, two plates, and a teapot with a classic ivory glaze. Each piece is hand-thrown and fired at high temperatures for durability. Perfect for a cozy morning or a formal afternoon tea.", "k2.jpg", "Kitchen", "Kaira Advani", 4.5);
        update(toUpdate, existing, "Blue Ceramic Teapot", "This artisanal teapot features a deep cobalt blue glaze with subtle earthy speckles. Its ergonomic handle and drip-free spout ensure a smooth pour every time. A beautiful addition to any kitchen counter.", "k3.jpg", "Kitchen", "Suresh Raina", 4.8);
        update(toUpdate, existing, "Handcrafted Spice Rack", "Keep your kitchen organized with this three-tier spice rack made from reclaimed teak wood. It includes 12 glass jars with airtight wooden lids, preserving the freshness and aroma of your spices.", "k4.jpg", "Kitchen", "Manish Pandey", 4.3);
        update(toUpdate, existing, "Marble Mortar & Pestle", "Grind herbs and spices like a professional chef with this heavy-duty solid marble set. The polished exterior and rough interior provide the perfect surface for crushing and blending flavors.", "k5.jpg", "Kitchen", "Hardik Pandya", 4.9);
        update(toUpdate, existing, "Bamboo Cutting Board", "A sustainable and knife-friendly cutting board with a built-in juice groove. Its large surface area makes it ideal for chopping vegetables or serving as a rustic cheese board.", "k6.jpg", "Kitchen", "Rohit Sharma", 4.6);
        update(toUpdate, existing, "Copper Mixing Bowls", "Set of three stainless steel bowls with a stunning hammered copper exterior. These bowls are not only functional for baking but also beautiful enough to use as serving pieces.", "k7.jpg", "Kitchen", "Virat Kohli", 4.4);
        update(toUpdate, existing, "Linen Apron", "A stylish and durable cross-back apron made from 100% natural linen. It features two deep front pockets and a comfortable fit that doesn't pull on your neck.", "k8.jpg", "Kitchen", "Shubman Gill", 4.2);
        update(toUpdate, existing, "Wooden Honey Dipper", "Drizzle honey without the mess using this hand-carved olive wood dipper. Its deep grooves are designed to hold onto honey until you're ready to pour.", "k9.jpg", "Kitchen", "KL Rahul", 4.1);
        update(toUpdate, existing, "Ceramic Egg Carton", "A reusable and eco-friendly way to store half a dozen eggs in your fridge. This hand-painted ceramic tray adds a vintage touch to your kitchen organization.", "k10.jpg", "Kitchen", "Rishabh Pant", 4.5);
        update(toUpdate, existing, "Glass Herb Infuser", "Infuse oils and vinegars with fresh herbs and garlic using this elegant glass bottle with a built-in strainer. A must-have for home cooks who love bold flavors.", "k11.jpg", "Kitchen", "Jasprit Bumrah", 4.3);
        update(toUpdate, existing, "Terracotta Bread Warmer", "Keep your bread warm throughout dinner with this engraved terracotta stone. Simply heat it in the oven and place it at the bottom of your bread basket.", "k12.jpg", "Kitchen", "Mohammed Shami", 4.6);
        update(toUpdate, existing, "Woven Table Runner", "Add a touch of bohemian style to your dining table with this hand-woven cotton runner. Its intricate patterns and fringed edges complement any decor.", "k13.jpg", "Kitchen", "Ravindra Jadeja", 4.4);

        // --- HOME DECOR (d1-d13) ---
        update(toUpdate, existing, "Boho Macrame Wall Hanging", "Elevate your living space with this intricate macrame wall art, handcrafted with 100% natural cotton cord. The geometric patterns and flowing fringe add a cozy, bohemian vibe to any room. It hangs gracefully on a polished wooden dowel, making it an easy addition to your bedroom or gallery wall.", "d1.jpg", "Home Decor", "Ishaan Khatter", 4.8);
        update(toUpdate, existing, "Artisanal Scented Candle", "Fill your home with the calming aroma of lavender and sandalwood. This soy wax candle is hand-poured into a reusable ceramic jar and features a crackling wood wick for a fireplace-like ambiance.", "d2.jpg", "Home Decor", "Ananya Panday", 4.6);
        update(toUpdate, existing, "Woven Seagrass Basket", "A versatile storage solution that doubles as a stylish planter. This sturdy basket is hand-woven by skilled artisans and features reinforced handles for easy carrying.", "d3.jpg", "Home Decor", "Sara Ali Khan", 4.5);
        update(toUpdate, existing, "Crystal Geode Bookends", "Add a touch of natural luxury to your bookshelf with these heavy amethyst geode halves. Each piece is unique, showcasing brilliant purple crystals that sparkle in the light.", "d4.jpg", "Home Decor", "Janhvi Kapoor", 4.9);
        update(toUpdate, existing, "Vintage Brass Mirror", "A classic round mirror with a hand-etched brass frame. Its timeless design adds depth and character to your entryway or vanity area.", "d5.jpg", "Home Decor", "Kartik Aaryan", 4.4);
        update(toUpdate, existing, "Embroidered Silk Cushion", "Luxurious silk cushion cover featuring intricate hand-embroidered floral motifs. It adds a pop of color and a touch of elegance to your sofa or armchair.", "d6.jpg", "Home Decor", "Ayushmann Khurrana", 4.3);
        update(toUpdate, existing, "Modern Ceramic Vase", "A minimalist matte white vase with a unique asymmetrical shape. Ideal for displaying dried pampas grass or a single fresh bloom.", "d7.jpg", "Home Decor", "Ranbir Kapoor", 4.7);
        update(toUpdate, existing, "Hand-tufted Rug", "A soft and durable area rug with a bold geometric pattern. Hand-tufted from 100% New Zealand wool, it provides warmth and comfort underfoot.", "d8.jpg", "Home Decor", "Alia Bhatt", 4.6);
        update(toUpdate, existing, "Sculptural Iron Lamp", "An industrial-style table lamp with a hand-forged iron base and a warm Edison bulb. It creates a cozy and sophisticated atmosphere in any study or living room.", "d9.jpg", "Home Decor", "Ranveer Singh", 4.5);
        update(toUpdate, existing, "Botanical Framed Print", "A high-quality reproduction of a vintage botanical illustration, professionally framed in a slim oak wood frame. Perfect for a nature-inspired gallery wall.", "d10.jpg", "Home Decor", "Deepika Padukone", 4.2);
        update(toUpdate, existing, "Terrarium Kit", "Create your own miniature garden with this all-inclusive kit. Includes a geometric glass container, soil, pebbles, and moss. Just add your favorite succulents!", "d11.jpg", "Home Decor", "Vicky Kaushal", 4.8);
        update(toUpdate, existing, "Driftwood Wall Clock", "A unique and rustic wall clock crafted from pieces of natural driftwood. Its silent movement and minimalist hands make it a functional work of art.", "d12.jpg", "Home Decor", "Katrina Kaif", 4.4);
        update(toUpdate, existing, "Hand-painted Tile Coasters", "Set of four ceramic coasters featuring traditional hand-painted motifs. Each coaster is cork-backed to protect your furniture from heat and moisture.", "d13.jpg", "Home Decor", "Shahid Kapoor", 4.3);

        // --- JEWELRY (j1-j13) ---
        update(toUpdate, existing, "Silver Moonstone Ring", "A delicate sterling silver ring featuring a high-quality iridescent moonstone. The stone glows with a mysterious blue light, symbolizing intuition and protection. Hand-polished to a brilliant shine, it’s a perfect statement piece for any occasion.", "j1.jpg", "Jewelry", "Sonam Kapoor", 4.9);
        update(toUpdate, existing, "Gold Filigree Earrings", "Stunning 18k gold-plated earrings with intricate filigree work. These lightweight earrings are inspired by traditional Indian motifs and add a touch of regality to any outfit.", "j2.jpg", "Jewelry", "Kareena Kapoor", 4.7);
        update(toUpdate, existing, "Pearl Pendant Necklace", "A classic and elegant necklace featuring a genuine freshwater pearl on a fine 14k gold chain. A timeless gift that symbolizes purity and grace.", "j3.jpg", "Jewelry", "Priyanka Chopra", 4.8);
        update(toUpdate, existing, "Beaded Boho Bracelet", "A colorful multi-strand bracelet made with tiny glass seed beads and a secure magnetic clasp. Perfect for layering with other bracelets for a stacked look.", "j4.jpg", "Jewelry", "Shraddha Kapoor", 4.4);
        update(toUpdate, existing, "Hammered Copper Cuff", "A bold and rustic cuff bracelet made from pure hammered copper. Over time, it will develop a beautiful natural patina, making it truly one-of-a-kind.", "j5.jpg", "Jewelry", "Tiger Shroff", 4.3);
        update(toUpdate, existing, "Emerald Stud Earrings", "Vibrant green lab-created emeralds set in minimalist 925 sterling silver studs. A pop of luxury for everyday wear.", "j6.jpg", "Jewelry", "Varun Dhawan", 4.6);
        update(toUpdate, existing, "Turquoise Statement Necklace", "A chunky necklace featuring large turquoise stones and silver-tone accents. It brings a Southwestern flair to a simple white tee or a flowing dress.", "j7.jpg", "Jewelry", "Sidharth Malhotra", 4.5);
        update(toUpdate, existing, "Amethyst Drop Earrings", "Elegant teardrop-shaped amethyst stones suspended from delicate silver wires. The deep purple hue is said to promote calm and clarity.", "j8.jpg", "Jewelry", "Kiara Advani", 4.7);
        update(toUpdate, existing, "Rose Gold Initial Ring", "A personalized ring featuring a dainty initial on a thin rose gold band. A thoughtful and stylish gift for yourself or a loved one.", "j9.jpg", "Jewelry", "Kriti Sanon", 4.2);
        update(toUpdate, existing, "Leather Braided Bracelet", "A rugged and masculine bracelet made from high-quality braided leather with a stainless steel anchor clasp.", "j10.jpg", "Jewelry", "Hrithik Roshan", 4.5);
        update(toUpdate, existing, "Labradorite Hoop Earrings", "Modern hoops adorned with small labradorite beads that flash with hidden color when they catch the light.", "j11.jpg", "Jewelry", "Tara Sutaria", 4.4);
        update(toUpdate, existing, "Raw Quartz Crystal Point", "A natural clear quartz crystal point on a simple silver cord. This necklace celebrates the raw beauty of earth's creations.", "j12.jpg", "Jewelry", "Disha Patani", 4.6);
        update(toUpdate, existing, "Minimalist Bar Necklace", "A sleek and polished horizontal bar on a fine silver chain. Perfect for engraving a special date or a meaningful word.", "j13.jpg", "Jewelry", "Arjun Kapoor", 4.3);

        // --- POTTERY (po1-po10) ---
        update(toUpdate, existing, "Hand-thrown Coffee Mug", "Start your morning with this large, earthy coffee mug, hand-thrown on a potter’s wheel and finished with a unique speckled glaze. The wide handle provides a comfortable grip, while the thick ceramic walls keep your drink hot for longer. Each mug is individually crafted, so no two are exactly alike.", "po1.jpg", "Pottery", "Rajkumar Rao", 4.7);
        update(toUpdate, existing, "Speckled Serving Bowl", "A large and shallow bowl perfect for serving salads or pasta. The cream glaze is accented with dark iron spots, giving it a rustic and modern feel.", "po2.jpg", "Pottery", "Ayushmann Khurrana", 4.6);
        update(toUpdate, existing, "Mini Succulent Planters", "Set of three tiny ceramic pots with built-in drainage holes. These cute planters are ideal for small succulents or cacti on a sunny windowsill.", "po3.jpg", "Pottery", "Pankaj Tripathi", 4.5);
        update(toUpdate, existing, "Ribbed Clay Vase", "A tall and slender vase with a distinctive ribbed texture. Its natural terracotta color complements any flower arrangement.", "po4.jpg", "Pottery", "Nawazuddin Siddiqui", 4.4);
        update(toUpdate, existing, "Glazed Incense Holder", "A minimalist incense burner with a teardrop shape that catches all the ash. Hand-dipped in a calming celadon green glaze.", "po5.jpg", "Pottery", "Manoj Bajpayee", 4.8);
        update(toUpdate, existing, "Geometric Fruit Bowl", "A striking centerpiece for your kitchen island. This bowl features a faceted geometric design that adds a modern architectural touch to your home.", "po6.jpg", "Pottery", "Vijay Varma", 4.3);
        update(toUpdate, existing, "Blue Ceramic Teacup", "An elegant teacup without a handle, inspired by traditional Japanese tea ceremonies. Its smooth glaze feels wonderful in your hands.", "po7.jpg", "Pottery", "Siddhant Chaturvedi", 4.5);
        update(toUpdate, existing, "Textured Wall Planter", "Save space and add greenery to your walls with this flat-backed ceramic planter. Its rough, sandy texture provides a nice contrast to green leaves.", "po8.jpg", "Pottery", "Vikrant Massey", 4.2);
        update(toUpdate, existing, "Rustic Pitcher", "A hand-formed pitcher with a wide spout and a sturdy handle. Perfect for serving cold water or lemonade during a summer gathering.", "po9.jpg", "Pottery", "Jaideep Ahlawat", 4.6);
        update(toUpdate, existing, "Hand-painted Plate Set", "Set of four small dessert plates, each featuring a different hand-painted botanical motif. A delightful way to end any meal.", "po10.jpg", "Pottery", "Pratik Gandhi", 4.4);
        
        saveBatch(toUpdate);
    }

    private void update(List<Product> toSave, List<Product> existing, String name, String desc, String img, String cat, String art, Double rate) {
        Product p = existing.stream()
                .filter(ext -> ext.getName().equals(name))
                .findFirst()
                .orElse(new Product());
        
        p.setName(name);
        p.setDescription(desc);
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
