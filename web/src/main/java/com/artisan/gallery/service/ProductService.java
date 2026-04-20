package com.artisan.gallery.service;

import com.artisan.gallery.model.Product;
import com.artisan.gallery.repository.ProductRepository;
import com.artisan.gallery.repository.ReviewRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        if ("All".equals(category) || category == null || category.isEmpty()) {
            return repository.findAll();
        }
        return repository.findByCategory(category);
    }

    public List<Product> getSystemProducts() {
        return repository.findByUploader("System");
    }

    public List<Product> getSharedCreations(String currentUserEmail) {
        if (currentUserEmail == null) return repository.findByUploaderNot("System");
        return repository.findByUploaderNotAndUploaderNot("System", currentUserEmail);
    }
    
    public List<Product> getUserItems(String email) {
        if (email == null) return new ArrayList<>();
        return repository.findByUploader(email);
    }

    public long getUserUploadCount(String email) {
        if (email == null) return 0;
        return repository.countByUploader(email);
    }

    public List<Product> searchProducts(String query) {
        return repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query, query);
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void saveProduct(Product product) {
        repository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        reviewRepository.deleteByProductId(id);
        repository.deleteById(id);
    }

    public List<Product> getSuggestions(String category, Long excludeId) {
        return repository.findByCategory(category).stream()
                .filter(p -> !p.getId().equals(excludeId))
                .limit(5)
                .toList();
    }

    @PostConstruct
    public void initData() {
        // Force update existing items to have diverse ratings and stock status
        repository.findAll().forEach(p -> {
            // Assign specific out of stock items
            if ("Fruit Still Life".equals(p.getName()) || 
                "Starry Night Vase".equals(p.getName()) || 
                "Songbird Pitcher".equals(p.getName())) {
                p.setInStock(false);
            } else {
                p.setInStock(true);
            }

            // Assign diverse ratings based on index/name to make them look realistic
            double[] ratings = {3.5, 4.0, 4.2, 4.5, 4.8, 5.0};
            int index = Math.abs(p.getName().hashCode()) % ratings.length;
            p.setRating(ratings[index]);
            
            repository.save(p);
        });

        if (repository.count() > 0) return;

        List<Product> products = new ArrayList<>();
        
        // --- PAINTINGS (p1-p10) ---
        addP(products, "Fruit Still Life", "Vibrant oil painting of fresh apples and grapes on a small easel.", "p1.jpg", "Paintings", "Anita Sharma", 4.8, false);
        addP(products, "The Knight's Vow", "A romantic classical painting depicting a knight and a princess.", "p2.jpg", "Paintings", "Rajesh Kumar", 4.5);
        addP(products, "Coastal Village", "Brightly colored houses of a Mediterranean town on canvas.", "p3.jpg", "Paintings", "Siddharth Verma", 4.9);
        addP(products, "Golden Moonbeam", "Serene night sky with a large golden moon reflected on the sea.", "p4.jpg", "Paintings", "Neha Patel", 4.2);
        addP(products, "The Mermaid's Grace", "Ethereal underwater portrait of a mermaid in deep blue waters.", "p5.jpg", "Paintings", "Amitabh Gupta", 4.7);
        addP(products, "Flamenco Soul", "Dynamic painting of a dancer in a red dress capturing movement and passion.", "p6.jpg", "Paintings", "Priya Singh", 4.4);
        addP(products, "Royal Peacock", "Textured acrylic painting of a peacock with a stunning colorful tail.", "p7.jpg", "Paintings", "Vikram Malhotra", 4.6);
        addP(products, "The Forbidden Castle", "Atmospheric dark fantasy landscape featuring a castle in a misty forest.", "p8.jpg", "Paintings", "Deepika Iyer", 4.3);
        addP(products, "Voyage into the Unknown", "A tall ship seen through a rocky cave opening, oil on canvas.", "p9.jpg", "Paintings", "Arjun Reddy", 4.1);
        addP(products, "Midnight Lantern Street", "A solitary lamp post lighting up a dark, moody street scene.", "p10.jpg", "Paintings", "Maya Deshmukh", 4.9);

        // --- POTTERY (po1-po10) ---
        addP(products, "Starry Night Vase", "Hand-painted ceramic vase inspired by Van Gogh's masterpiece.", "po1.jpg", "Pottery", "Sunita Rao", 5.0, false);
        addP(products, "Songbird Pitcher", "Rustic hand-painted pitcher featuring a detailed woodland bird.", "po2.jpg", "Pottery", "Karan Johar", 4.3, false);
        addP(products, "Field of Sunflowers Vase", "Tall ceramic vase adorned with bright, sunny sunflower motifs.", "po3.jpg", "Pottery", "Lata Mangeshkar", 4.7);
        addP(products, "Moroccan Sunset Jar", "Intricately designed ceramic jar with vibrant traditional patterns.", "po4.jpg", "Pottery", "Rohan Joshi", 4.4);
        addP(products, "White Lily Base", "Elegant ceramic lamp base shaped like delicate lily petals.", "po5.jpg", "Pottery", "Tanu Shree", 4.2);
        addP(products, "Sweet Ribbon Vase", "Light blue ceramic vase with a sculpted pink ribbon detail.", "po6.jpg", "Pottery", "Isha Ambani", 4.8);
        addP(products, "Winter Rose Teapot", "Exquisite teapot shaped like a blooming blue rose with leaf saucers.", "po7.jpg", "Pottery", "Kabir Khan", 4.6);
        addP(products, "Bloom Tea Spoons", "Set of four ceramic spoons and saucers shaped like delicate spring flowers.", "po8.jpg", "Pottery", "Meera Nair", 4.1);
        addP(products, "Starry Sky Tea Set", "Complete ceramic tea service with celestial patterns and golden accents.", "po9.jpg", "Pottery", "Nitin Gadkari", 4.5);
        addP(products, "Galaxy Plate", "Decorative ceramic plate featuring a swirling nebula of blues and golds.", "po10.jpg", "Pottery", "Omkar Nath", 4.9);

        // --- CROCHET (cr1-cr10) ---
        addP(products, "Garden Keychain Set", "Assortment of tiny crocheted fruits and flowers for your keys.", "cr1.jpg", "Crochet", "Palak Muchhal", 4.2);
        addP(products, "Everlasting Bouquet", "Hand-knitted yarn flowers in a beautiful blue and white arrangement.", "cr2.jpg", "Crochet", "Qasim Ali", 4.7);
        addP(products, "Petal Patchwork Pillow", "Cozy crochet cushion with 3D floral designs in soft pink tones.", "cr3.jpg", "Crochet", "Riya Sen", 4.4);
        addP(products, "Spring Blossom Scarf", "Lightweight crochet scarf featuring delicate pink flowers.", "cr4.jpg", "Crochet", "Sahil Khan", 4.0);
        addP(products, "Starfish Beach Bag", "Cream-colored crochet handbag adorned with a cute pink starfish charm.", "cr5.jpg", "Crochet", "Tara Sutaria", 4.6);
        addP(products, "Sky Blue Summer Dress", "Intricate crochet dress with a lacy pattern, perfect for sunny days.", "cr6.jpg", "Crochet", "Zoya Akhtar", 4.9);
        addP(products, "Forest Fairy Headband", "Dainty crochet headband with tiny white flowers and trailing vines.", "cr7.jpg", "Crochet", "Yash Chopra", 4.3);
        addP(products, "Butterfly Wall Tapestry", "Large-scale crochet wall hanging featuring a colorful butterfly design.", "cr8.jpg", "Crochet", "Varun Dhawan", 4.5);
        addP(products, "Veggie Pencil Pouches", "Fun and quirky crochet cases shaped like carrots and peas.", "cr9.jpg", "Crochet", "Udit Narayan", 4.1);
        addP(products, "Boho Plant Hanger", "Sturdy macrame-style crochet hanger for your favorite indoor plants.", "cr10.jpg", "Crochet", "Tushar Kapoor", 4.8);

        // --- CRAFT (c1-c10) ---
        addP(products, "Enchanted Treehouse", "Whimsical house built into a tree with intricate details.", "c1.jpg", "Craft", "Sanjay Leela", 5.0);
        addP(products, "Crystal Sun Catcher", "Hand-crafted hanging decor that creates rainbows in sunlight.", "c2.jpg", "Craft", "Rishi Kapoor", 4.4);
        addP(products, "Butterfly Specimen Frame", "Elegant wire-art butterflies mounted in a minimalist frame.", "c3.jpg", "Craft", "Pankaj Kapur", 4.2);
        addP(products, "Suitcase Dream Room", "A miniature bedroom meticulously crafted inside a vintage suitcase.", "c4.jpg", "Craft", "Nana Patekar", 4.9);
        addP(products, "Eco Fairy Cottage", "Creative fairy house built from a recycled detergent bottle.", "c5.jpg", "Craft", "Manoj Bajpayee", 4.7);
        addP(products, "Quilled Floral Heart", "Intricate paper quilling art forming a vibrant heart bouquet.", "c6.jpg", "Craft", "Savitri Devi", 4.1);
        addP(products, "Wire Fish Bowl", "Unique desktop decor featuring wire-sculpted fish in a glass bowl.", "c7.jpg", "Craft", "Gopal Das", 4.3);
        addP(products, "Matchbox Memories", "Tiny, detailed scenes crafted inside sliding matchboxes.", "c8.jpg", "Craft", "Ishwar Singh", 4.6);
        addP(products, "Ocean Depths Shadow Box", "3D paper-cut art depicting a serene underwater world.", "c9.jpg", "Craft", "Meenakshi Jain", 4.5);
        addP(products, "Tropical Flower Anklet", "Hand-crafted barefoot sandal jewelry with white plumeria.", "c10.jpg", "Craft", "Harish Verma", 4.0);

        // --- VARIETY/OTHERS (v1-v19) ---
        addP(products, "Autumn Vine Foot Jewelry", "Delicate wire-wrapped vine accessory for your ankles.", "v1.jpg", "Others", "Kavita Reddy", 4.4);
        addP(products, "Moonlit Bonsai Lamp", "Decorative tree sculpture with a glowing moon backdrop.", "v2.jpg", "Others", "Suresh Prabhu", 4.9);
        addP(products, "Fairy Glow Lantern", "Enchanting glass jar containing a miniature fairy scene.", "v3.jpg", "Others", "Anjali Menon", 4.7);
        addP(products, "Mini Macrame Magnets", "Set of tiny handmade macrame hangings for your fridge.", "v4.jpg", "Others", "Ramesh Babu", 4.1);
        addP(products, "Driftwood Shell Chime", "Wall hanging made from seashells and weathered driftwood.", "v5.jpg", "Others", "Shanti Swaroop", 4.2);
        addP(products, "Gourmet Kitchen Clock", "Wall clock decorated with realistic polymer clay food.", "v6.jpg", "Others", "Abhay Deol", 4.5);
        addP(products, "Emerald Elven Ear Cuff", "Intricate wire-wrapped jewelry with green crystal accents.", "v7.jpg", "Others", "Bipasha Basu", 4.8);
        addP(products, "Royal Butterfly Hand Chain", "Elegant emerald jewelry connecting a ring and a bracelet.", "v8.jpg", "Others", "Chitrangada Singh", 4.6);
        addP(products, "Dewdrop Crystal Tiara", "Dainty wire-wrapped crown adorned with clear crystal beads.", "v9.jpg", "Others", "Dia Mirza", 4.3);
        addP(products, "Opalescent Butterfly Wings", "Stunning wall decor with shimmering, iridescent wings.", "v10.jpg", "Others", "Emraan Hashmi", 4.7);
        addP(products, "White Jasmine Hand Chain", "Romantic bridal hand accessory with delicate white flowers.", "v11.jpg", "Others", "Farhan Akhtar", 4.9);
        addP(products, "Golden Laurel Arm Cuff", "Adjustable upper arm bracelet with a classic gold leaf design.", "v12.jpg", "Others", "Gita Bali", 4.2);
        addP(products, "Penguin Perpetual Calendar", "Cute wooden desk accessory to keep track of the date.", "v13.jpg", "Others", "Hrithik Roshan", 4.0);
        addP(products, "Cozy Campfire Miniatures", "Set of miniature polymer clay stones and fire for dollhouses.", "v14.jpg", "Others", "Inder Kumar", 4.4);
        addP(products, "Artisan Chip & Dip Bowl", "Handmade ceramic bowl designed for snacks and sauces.", "v15.jpg", "Others", "Juhi Chawla", 4.6);
        addP(products, "English Ivy Jewelry Stand", "Decorative rack for earrings shaped like climbing ivy vines.", "v17.jpg", "Others", "Kailash Kher", 4.1);
        addP(products, "Pink Coral Jewelry Tree", "Vibrant coral-shaped stand for organizing your jewelry.", "v18.jpg", "Others", "Lara Dutta", 4.5);
        addP(products, "Calla Lily Candle Vase", "Ceramic sculpture that serves as both a vase and a candle holder.", "v19.jpg", "Others", "Om Puri", 4.8);

        repository.saveAll(products);
    }

    private void addP(List<Product> list, String name, String desc, String img, String cat, String artist, double rating) {
        addP(list, name, desc, img, cat, artist, rating, true);
    }

    private void addP(List<Product> list, String name, String desc, String img, String cat, String artist, double rating, boolean inStock) {
        Random r = new Random();
        Product p = new Product(
            name, desc, (double)(500 + r.nextInt(4500)), img, cat, artist, rating, 10 + r.nextInt(50), "2-4 Days", "System"
        );
        p.setInStock(inStock);
        list.add(p);
    }
}
