package com.artisan.gallery.service;

import com.artisan.gallery.model.Product;
import com.artisan.gallery.repository.ProductRepository;
import com.artisan.gallery.repository.CartItemRepository;
import com.artisan.gallery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
                // Wait for DB to be ready
                Thread.sleep(5000); 
                System.out.println(">>> [STARTUP] Syncing system items by Image ID to fix mismatches...");
                ProductService self = selfProvider.getIfAvailable();
                if (self != null) {
                    self.performSafeUpdate();
                }
                System.out.println(">>> [STARTUP] Mismatches resolved and descriptions updated!");
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Sync failed: " + e.getMessage());
            }
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    @Transactional
    public void performSafeUpdate() {
        List<Product> systemProducts = repository.findByUploader("System");
        
        // Key by Image ID (e.g., "v17") to ensure we update the CORRECT record
        Map<String, Product> existingByImgId = new HashMap<>();
        for (Product p : systemProducts) {
            if (p.getImageUrl() != null && p.getImageUrl().contains("/")) {
                String imgId = p.getImageUrl().substring(p.getImageUrl().lastIndexOf("/") + 1);
                existingByImgId.put(imgId, p);
            }
        }
        
        List<Product> toSave = new ArrayList<>();
        String baseUrl = "https://res.cloudinary.com/dpt2wn9lh/image/upload/";

        java.util.function.BiConsumer<String, String[]> updater = (name, data) -> {
            String imgId = data[1];
            // Find existing product by its Image ID, or create new if it doesn't exist
            Product p = existingByImgId.getOrDefault(imgId, new Product());
            
            p.setName(name);
            p.setDescription(data[0]);
            p.setImageUrl(baseUrl + imgId);
            p.setCategory(data[2]);
            p.setArtist(data[3]);
            p.setRating(Double.parseDouble(data[4]));
            p.setInStock(Boolean.parseBoolean(data[5]));
            
            if (p.getId() == null) {
                p.setUploader("System");
                p.setPrice((double)(1000 + new Random().nextInt(4000)));
                p.setReviewCount(15 + new Random().nextInt(80));
                p.setDeliveryTime("3-5 Days");
            }
            toSave.add(p);
        };

        // --- CORRECTED MAPPINGS & LONG DESCRIPTIONS ---

        // PAINTINGS (p1-p10)
        updater.accept("Harvest Bounty Still Life", new String[]{"This exquisite oil painting captures the vibrant essence of a fresh harvest. Every brushstroke meticulously details the texture of ripened fruits, from the velvety skin of peaches to the glistening surface of grapes. It is a timeless masterpiece that brings the warmth and abundance of nature into any living space, serving as a focal point of artistic elegance and classical beauty.", "p1", "Paintings", "Anita Sharma", "4.8", "false"});
        updater.accept("The Valiant Knight's Vow", new String[]{"Step back into a world of chivalry and romance with this classical masterpiece. The painting depicts a noble knight in a moment of solemn devotion, rendered with a rich palette and dramatic lighting that evokes the grandeur of historical epics. It is an ideal piece for collectors who appreciate traditional storytelling and the fine detail of classical art techniques and oil painting mastery.", "p2", "Paintings", "Rajesh Kumar", "4.5", "true"});
        updater.accept("Sun-Drenched Coastal Village", new String[]{"Transport yourself to the serene shores of the Mediterranean with this vibrant acrylic painting. The artwork features charming hillside houses overlooking a sparkling azure sea, captured with bold colors and a sense of light that feels almost tangible. This piece is perfect for adding a touch of coastal charm and a relaxed, breezy atmosphere to your home decor and interior design.", "p3", "Paintings", "Siddharth Verma", "4.9", "true"});
        updater.accept("Enchanted Golden Moonbeam", new String[]{"A serene and captivating exploration of the night sky, this painting focuses on the ethereal beauty of a full moon. The artist uses a delicate blending of blues and golds to create a glowing effect that seems to radiate from the canvas. It is a peaceful and contemplative piece that invites the viewer to lose themselves in the quiet magic of the night and celestial wonder.", "p4", "Paintings", "Neha Patel", "4.2", "true"});
        updater.accept("The Graceful Mermaid's Song", new String[]{"Dive into an ethereal underwater world with this stunning portrait of a mermaid. The painting combines soft, flowing lines with a shimmering color palette to depict a sense of grace and mystery beneath the waves. It is a whimsical and imaginative artwork that appeals to the dreamer in everyone, perfect for a nursery or a creative workspace filled with fantasy and color.", "p5", "Paintings", "Amitabh Gupta", "4.7", "false"});
        updater.accept("Spirit of the Flamenco Soul", new String[]{"This dynamic painting captures the raw energy and passion of a Spanish flamenco dancer. The use of bold reds and deep shadows creates a sense of movement and intensity that is truly captivating. Every detail, from the swirling fabric of the dress to the expressive posture of the dancer, tells a story of cultural heritage, artistic fire, and rhythmic beauty.", "p6", "Paintings", "Priya Singh", "4.4", "true"});
        updater.accept("Majestic Royal Peacock Art", new String[]{"Celebrate the splendor of nature with this textured acrylic painting of a royal peacock. The artist uses a combination of palette knife techniques and rich pigments to create a three-dimensional effect that brings the bird's feathers to life. This piece is a bold statement of beauty and luxury, suitable for any sophisticated interior design that values nature and craftsmanship.", "p7", "Paintings", "Vikram Malhotra", "4.6", "true"});
        updater.accept("Secret of the Forbidden Castle", new String[]{"Embark on a journey into a dark fantasy landscape with this atmospheric painting. A mysterious castle looms in the distance, surrounded by mist and rugged terrain, creating a sense of awe and intrigue. The artist's mastery of light and shadow builds a compelling narrative that sparks the imagination and invites exploration of unknown realms and ancient legends.", "p8", "Paintings", "Deepika Iyer", "4.3", "true"});
        updater.accept("Legendary Voyage into Unknown", new String[]{"This adventurous painting depicts a majestic ship navigating through a colossal sea cave. The contrast between the dark, towering rock formations and the sunlit opening of the cave creates a dramatic sense of scale and wonder. It is a powerful representation of discovery and the human spirit's desire to explore the furthest reaches of the world and maritime history.", "p9", "Paintings", "Arjun Reddy", "4.7", "true"});
        updater.accept("Bioluminescent Mystic Forest", new String[]{"Experience the magic of an enchanted woodland with this bioluminescent forest art. The painting features glowing flora and fauna that illuminate the dark forest floor, creating a dreamlike and otherworldly atmosphere. It is a unique and captivating piece that transforms any room into a gateway to a world of fantasy and natural wonder, perfect for meditation and calm.", "p10", "Paintings", "Ishani Bose", "4.6", "true"});

        // OTHERS / KITCHEN (v1-v13)
        updater.accept("Verdant Leafy Tea Infuser", new String[]{"Make your tea time more enjoyable with this charming leafy tea infuser. Crafted from high-quality, food-grade silicone, it is designed to look like a fresh sprout emerging from your cup. It is easy to clean, durable, and perfect for loose leaf tea lovers who appreciate a touch of nature-inspired design in their daily kitchen essentials and sustainable living habits.", "v1", "Others", "Karan Kapur", "4.7", "true"});
        updater.accept("Classic English Breakfast Set", new String[]{"Start your morning with elegance using this handcrafted ceramic breakfast set. The set includes a perfectly sized mug, a bowl, and a side plate, all featuring a timeless minimalist design and a smooth, glazed finish. It is durable enough for everyday use yet stylish enough to impress guests during a weekend brunch or a quiet morning at home with your favorite meal.", "v2", "Others", "Kaira Advani", "4.5", "true"});
        updater.accept("Artisanal Blue Ceramic Teapot", new String[]{"This beautiful blue ceramic teapot is a testament to artisanal craftsmanship. Its unique textured surface and deep cobalt hue make it a standout piece for any tea service. Designed for both beauty and functionality, it features an ergonomic handle and a drip-free spout, ensuring a perfect pour every time you host a tea party or enjoy a relaxing afternoon cup.", "v3", "Others", "Suresh Raina", "4.8", "true"});
        updater.accept("Handcrafted Teak Spice Rack", new String[]{"Organize your kitchen in style with this handcrafted teak wood spice rack. Each piece is unique, showcasing the natural grain and warmth of high-quality teak. It is designed to be both space-saving and decorative, providing easy access to your favorite seasonings while adding a touch of rustic charm to your culinary workspace and home organization system.", "v4", "Others", "Manish Pandey", "4.3", "true"});
        updater.accept("Solid Marble Mortar & Pestle", new String[]{"Achieve the perfect grind for your spices and herbs with this solid marble mortar and pestle set. The heavy-duty construction ensures stability during use, while the smooth interior surface allows for efficient crushing and blending. It is a timeless kitchen tool that combines practical utility with the luxurious aesthetic of natural stone, making it a must-have for any gourmet cook.", "v5", "Others", "Hardik Pandya", "4.9", "true"});
        updater.accept("Sustainable Bamboo Cutting Board", new String[]{"Prepare your meals on this eco-friendly and sustainable bamboo cutting board. Bamboo is naturally antimicrobial and gentle on your knives, making it the ideal surface for all your chopping needs. The board features a sleek, modern design with a convenient handle, making it easy to move from counter to table for serving fresh appetizers and bread.", "v6", "Others", "Rohit Sharma", "4.6", "true"});
        updater.accept("Hammered Copper Mixing Bowls", new String[]{"Add a touch of professional flair to your kitchen with these hammered copper mixing bowls. The set includes various sizes, each featuring a stunning hand-hammered finish that develops a beautiful patina over time. These bowls are not only functional for prep work but also double as elegant serving dishes for salads, sides, and snacks at your next dinner party.", "v7", "Others", "Virat Kohli", "4.4", "true"});
        updater.accept("Natural Linen Artisan Apron", new String[]{"Protect your clothes while crafting or cooking with this natural linen artisan apron. Made from breathable and durable linen, it features adjustable straps and large pockets for your tools. The minimalist design and neutral color palette make it a stylish and practical choice for any creative endeavor, from gardening to gourmet cooking and professional art projects.", "v8", "Others", "Shubman Gill", "4.2", "true"});
        updater.accept("Olive Wood Honey Dipper", new String[]{"Drizzle honey perfectly with this handcrafted olive wood honey dipper. The unique deep grooves are designed to hold honey effectively, allowing for a mess-free application on your toast, tea, or yogurt. Crafted from a single piece of olive wood, it showcases beautiful natural patterns and is a charming addition to any breakfast table or kitchen decor.", "v9", "Others", "KL Rahul", "4.1", "true"});
        updater.accept("Vintage Ceramic Egg Carton", new String[]{"Keep your eggs organized and safe with this charming vintage-style ceramic egg carton. Unlike flimsy plastic or cardboard options, this reusable ceramic tray is durable and adds a touch of farmhouse elegance to your refrigerator or countertop. It is easy to clean and holds up to a dozen eggs in individual, secure slots, making it perfect for the organized kitchen.", "v10", "Others", "Rishabh Pant", "4.5", "true"});
        updater.accept("Glass Botanical Herb Infuser", new String[]{"Infuse your oils and vinegars with fresh herbs using this sleek glass botanical infuser. The bottle includes a built-in strainer that keeps the herbs contained while allowing the flavors to blend perfectly. Its elegant design makes it a beautiful addition to your dining table or kitchen shelf, perfect for homemade culinary creations and healthy cooking.", "v11", "Others", "Jasprit Bumrah", "4.3", "true"});
        updater.accept("Terracotta Traditional Bread Warmer", new String[]{"Keep your rolls and bread warm throughout dinner with this terracotta warming stone. Simply heat the stone in your oven and place it at the bottom of a bread basket. The natural terracotta retains heat exceptionally well, ensuring your baked goods stay soft and delicious from the first bite to the last, making it an essential for hosting family dinners.", "v12", "Others", "Mohammed Shami", "4.6", "true"});
        updater.accept("Hand-Woven Cotton Table Runner", new String[]{"Elevate your dining experience with this beautiful hand-woven cotton table runner. Featuring intricate patterns and a soft, natural texture, it adds a layer of warmth and sophistication to any table setting. Whether for a formal dinner party or a casual family meal, this runner provides a stylish foundation for your tableware and enhances your home decor.", "v13", "Others", "Ravindra Jadeja", "4.4", "true"});

        // CRAFT & DECOR (c1-c10, v14, v16-v19)
        updater.accept("Boho Macrame Dream Wall Hanging", new String[]{"Create a cozy and inviting atmosphere with this handcrafted macrame wall hanging. Intricately knotted from natural cotton rope, it features a beautiful bohemian design that adds texture and visual interest to any wall. It is a perfect statement piece for a bedroom, living room, or nursery, bringing a sense of handmade warmth and artistic flair to your home.", "c1", "Craft", "Ishaan Khatter", "4.8", "true"});
        updater.accept("Artisanal Lavender Scented Candle", new String[]{"Relax and unwind with the soothing aroma of this artisanal lavender scented candle. Hand-poured using premium soy wax and natural essential oils, it provides a clean and long-lasting burn. The minimalist glass jar design fits seamlessly into any decor, making it a thoughtful gift for yourself or a loved one in need of tranquility and stress relief.", "c2", "Craft", "Ananya Panday", "4.6", "true"});
        updater.accept("Woven Seagrass Storage Basket", new String[]{"Declutter your space with this versatile and stylish woven seagrass basket. Hand-crafted from sustainable materials, it is both durable and lightweight, perfect for storing blankets, toys, or magazines. The natural texture and earthy tones add a touch of organic beauty to any room while keeping your essentials organized and easily accessible in a stylish way.", "c3", "Craft", "Sara Ali Khan", "4.5", "true"});
        updater.accept("Natural Crystal Geode Bookends", new String[]{"Hold your favorite books in place with these stunning natural purple amethyst geode bookends. Each piece is unique, showcasing the raw beauty of crystal formations and deep violet hues. These bookends are not only functional but also serve as captivating decorative accents that bring the wonders of geology and natural art into your home library or office space.", "c4", "Craft", "Janhvi Kapoor", "4.9", "true"});
        updater.accept("Vintage Etched Brass Mirror", new String[]{"Add a touch of timeless elegance to your walls with this vintage-style etched brass mirror. The intricate floral motifs on the frame are meticulously handcrafted, reflecting a high level of artisanal skill and historical design. This mirror is a beautiful functional art piece that enhances the light and space in any room while serving as a sophisticated decorative element.", "c5", "Craft", "Kartik Aaryan", "4.4", "true"});
        updater.accept("Embroidered Silk Floral Cushion", new String[]{"Experience luxury with this exquisitely embroidered silk cushion cover. The delicate floral motifs are hand-stitched with vibrant threads on a high-quality silk base, creating a rich and textured finish. It is an ideal accent piece for a sofa or bed, adding a pop of color and a touch of traditional craftsmanship and elegance to your living space decor.", "c6", "Craft", "Ayushmann Khurrana", "4.3", "true"});
        updater.accept("Minimalist Modern Ceramic Vase", new String[]{"This minimalist matte white ceramic vase is a masterclass in modern design. Its clean lines and smooth finish make it a versatile piece that complements any interior style, from contemporary to Scandinavian. Whether displayed on its own or filled with a single stem, it adds a touch of understated sophistication and artistic form to your home decor.", "c7", "Craft", "Ranbir Kapoor", "4.7", "true"});
        updater.accept("Hand-Tufted Geometric Wool Rug", new String[]{"Enhance your floors with the soft comfort of this hand-tufted wool rug. Featuring a modern geometric pattern in neutral tones, it is designed to be both stylish and durable. The high-quality wool provides a plush feel underfoot, making it a perfect addition to a living room or bedroom where you want to create a cozy, warm, and inviting environment.", "c8", "Craft", "Alia Bhatt", "4.6", "true"});
        updater.accept("Sculptural Industrial Iron Lamp", new String[]{"Make a bold statement with this sculptural iron lamp featuring an industrial-inspired design. The rugged iron base and unique silhouette create a striking visual impact, perfect for an office or a modern living space. It provides warm, ambient lighting while serving as a conversation piece that showcases a love for unique, handcrafted, and industrial lighting solutions.", "c9", "Craft", "Ranveer Singh", "4.5", "true"});
        updater.accept("Vintage Botanical Framed Print", new String[]{"Bring the beauty of nature indoors with this vintage-style botanical framed print. The artwork features detailed illustrations of flora, captured with a sense of historical charm and scientific precision. It is professionally framed and ready to hang, making it an easy way to add a touch of classic natural beauty and artistic history to your study, kitchen, or hallway.", "c10", "Craft", "Deepika Padukone", "4.2", "true"});
        updater.accept("Complete DIY Terrarium Kit", new String[]{"Create your own miniature indoor garden with this complete DIY terrarium kit. The kit includes everything you need: a glass container, soil, pebbles, charcoal, and a variety of easy-to-care-for succulents or moss. It is a fun and rewarding project that allows you to bring a piece of nature into your home and enjoy the therapeutic benefits of indoor gardening and art.", "v14", "Craft", "Vicky Kaushal", "4.8", "true"});
        
        // FIX FOR v15 - "Bite Me" bowl
        updater.accept("\"Bite Me\" French Fry Ceramic Bowl", new String[]{"Add a touch of humor and personality to your snack time with this unique 'Bite Me' French fry bowl. Hand-painted with vibrant colors and a quirky design, this ceramic bowl is perfect for holding your favorite finger foods. It is a conversation starter at parties and a delightful gift for anyone who loves whimsical kitchenware and artisanal pottery with a modern twist.", "v15", "Pottery", "Katrina Kaif", "4.4", "true"});
        
        // FIX FOR v17 - Jewelry Stand (The one in the screenshot)
        updater.accept("Vine-Enchanted Ceramic Jewelry Stand", new String[]{"Organize your precious jewelry with this stunning Vine-Enchanted Ceramic Stand. Meticulously handcrafted, it features an elegant arched design adorned with hand-painted climbing vines. The built-in holes and ridges provide ample space for earrings, necklaces, and rings, transforming your vanity into a display of artistic botanical beauty. This piece is both a functional organizer and a work of ceramic art.", "v17", "Craft", "Shraddha Kapoor", "4.7", "true"});
        
        updater.accept("Floating Oak Wooden Wall Shelf", new String[]{"Showcase your favorite decor pieces on this elegant floating oak wall shelf. Crafted from solid oak with a smooth, natural finish, it provides a sturdy and stylish platform for books, plants, or photos. The hidden mounting system creates a clean, modern look that maximizes your wall space while adding the warmth and beauty of real wood and artisanal woodworking.", "v18", "Craft", "Varun Dhawan", "4.5", "true"});
        updater.accept("Vintage Metal Decorative Lantern", new String[]{"Create a warm and cozy glow with this vintage-style metal decorative lantern. Featuring intricate cut-out patterns and a distressed finish, it casts beautiful shadows when a candle is placed inside. It is a versatile piece that can be used as a centerpiece for a dining table or as an atmospheric accent on a patio or fireplace mantel for evenings at home.", "v19", "Craft", "Kriti Sanon", "4.6", "true"});

        // CROCHET & JEWELRY (cr1-cr10)
        updater.accept("Silver Moonstone Artisan Ring", new String[]{"This stunning sterling silver ring features a luminous moonstone, known for its ethereal glow and calming energy. Each ring is handcrafted with a delicate band that highlights the natural beauty of the gemstone. It is a perfect accessory for those who appreciate minimalist design and the unique qualities of natural stones, making it a meaningful addition to any jewelry collection.", "cr1", "Crochet", "Sonam Kapoor", "4.9", "true"});
        updater.accept("18k Gold Filigree Earrings", new String[]{"Elevate your look with these exquisitely crafted 18k gold-plated filigree earrings. The intricate lace-like patterns are achieved through traditional metalworking techniques, resulting in a design that is both lightweight and visually striking. These earrings are perfect for adding a touch of sophisticated elegance to a formal outfit or for elevating a casual everyday ensemble with artisanal charm.", "cr2", "Crochet", "Kareena Kapoor", "4.7", "true"});
        updater.accept("Classic Freshwater Pearl Pendant", new String[]{"Celebrate timeless beauty with this classic freshwater pearl pendant necklace. The single, high-luster pearl is suspended from a delicate sterling silver chain, creating a look that is elegant and versatile. It is a perfect piece for everyday wear or for special occasions, serving as a subtle yet sophisticated statement of grace and refined taste in high-quality jewelry.", "cr3", "Crochet", "Priyanka Chopra", "4.8", "true"});
        updater.accept("Multi-Strand Beaded Boho Bracelet", new String[]{"Add a pop of color and a bohemian vibe to your wrist with this multi-strand beaded bracelet. Hand-strung with a variety of colorful glass and wooden beads, it features a unique combination of textures and patterns. This bracelet is designed to be layered or worn alone, making it a fun and expressive accessory for any free-spirited fashion enthusiast and art lover.", "cr4", "Crochet", "Shraddha Kapoor", "4.4", "true"});
        updater.accept("Pure Hammered Copper Cuff", new String[]{"Make a statement with this bold and rustic hammered copper cuff. Handcrafted from pure copper, each piece features a unique texture created through traditional hammering techniques. Copper is believed by many to have healing properties, making this cuff not only a stylish accessory but also a meaningful one that develops a beautiful and unique patina over time with wear.", "cr5", "Crochet", "Tiger Shroff", "4.3", "true"});
        updater.accept("Green Emerald Silver Studs", new String[]{"These elegant silver stud earrings feature vibrant green emeralds, adding a touch of luxury and color to your ears. The stones are carefully set in high-quality sterling silver, ensuring durability and a timeless appeal. These studs are perfect for adding a subtle spark of color to your professional attire or for a special night out, making them a versatile staple in your accessory collection.", "cr6", "Crochet", "Varun Dhawan", "4.6", "true"});
        updater.accept("Chunky Turquoise Statement Necklace", new String[]{"Stand out from the crowd with this chunky turquoise statement necklace. The necklace features large, raw turquoise stones that showcase the natural variations in color and texture of this beloved gemstone. It is a bold and earthy piece that adds a touch of Southwestern charm and a powerful focal point to any simple outfit, perfect for making a memorable impression.", "cr7", "Crochet", "Sidharth Malhotra", "4.5", "true"});
        updater.accept("Teardrop Amethyst Drop Earrings", new String[]{"Add a touch of regal elegance with these beautiful teardrop amethyst drop earrings. The deep purple amethyst stones are cut to a perfect teardrop shape and suspended from delicate silver wires. These earrings catch the light beautifully, making them an ideal choice for special events where you want to add a sense of sophistication and refined color to your look.", "cr8", "Crochet", "Kiara Advani", "4.7", "true"});
        updater.accept("Dainty Rose Gold Initial Ring", new String[]{"Personalize your jewelry collection with this dainty rose gold initial ring. Crafted with a thin, elegant band and a precisely rendered initial, it is a perfect piece for layering or wearing as a subtle personal statement. The warm rose gold finish adds a touch of modern romance and makes it a thoughtful and personalized gift for a friend or loved one on any occasion.", "cr9", "Crochet", "Kriti Sanon", "4.2", "true"});
        
        // FIX FOR cr10 - Macrame Hanger (User mentioned it was mixed up)
        updater.accept("Boho-Chic Macrame Plant Hanger", new String[]{"Elevate your indoor jungle with this Boho-Chic Macrame Plant Hanger. Hand-knotted with premium natural cotton cord, it features intricate diamond patterns and a sturdy wooden ring for easy hanging. Designed to accommodate various pot sizes, this hanger adds a touch of bohemian elegance and vertical dimension to your living space, making it a favorite for plant enthusiasts.", "cr10", "Craft", "Hrithik Roshan", "4.5", "true"});

        // POTTERY (po1-po10)
        updater.accept("Speckled Wheel-Thrown Mug", new String[]{"Enjoy your favorite beverage in this unique speckled wheel-thrown ceramic mug. Each mug is individually crafted on a potter's wheel, ensuring a one-of-a-kind shape and feel in your hand. The speckled glaze adds a rustic and organic touch, making it a beautiful and functional piece of art that elevates your daily coffee or tea ritual at home and in the office.", "po1", "Pottery", "Rajkumar Rao", "4.7", "true"});
        updater.accept("Rustic Handmade Serving Bowl", new String[]{"Present your meals with style in this large, rustic handmade ceramic serving bowl. Its generous size and earthy glazed finish make it perfect for serving salads, pasta, or fruit at family gatherings. Each bowl is hand-formed, showcasing the marks of the maker and the natural beauty of the clay, adding a touch of artisanal charm to your dining table and kitchen.", "po2", "Pottery", "Ayushmann Khurrana", "4.6", "true"});
        updater.accept("Mini Succulent Ceramic Planters", new String[]{"Brighten up your desk or windowsill with this set of three tiny ceramic succulent planters. Each pot features a different textured design and a complementary color palette, perfect for holding small plants or air plants. These planters are hand-crafted and include drainage holes to ensure your succulents stay healthy while adding a touch of handmade greenery to your small spaces.", "po3", "Pottery", "Pankaj Tripathi", "4.5", "true"});
        updater.accept("Tall Ribbed Clay Vase", new String[]{"Make a statement with this tall, ribbed clay vase featuring a modern and architectural design. Its unique texture and warm, natural finish make it a striking decorative piece even without flowers. Hand-built using traditional coiling techniques, it showcases the versatility of clay and the skill of the artisan, adding a sense of height and artistic elegance to any room decor.", "po4", "Pottery", "Nawazuddin Siddiqui", "4.4", "true"});
        updater.accept("Celadon Glazed Incense Holder", new String[]{"Create a peaceful atmosphere with this elegant celadon green glazed incense holder. Its smooth, minimalist design is both functional and aesthetically pleasing, providing a secure place for your favorite incense sticks. The traditional celadon glaze creates a soft, luminous finish that reflects a sense of calm and refinement, perfect for meditation or simply relaxing at home after a long day.", "po5", "Pottery", "Manoj Bajpayee", "4.8", "true"});
        updater.accept("Modern Faceted Fruit Bowl", new String[]{"This modern faceted ceramic fruit bowl is a stunning example of contemporary pottery. The geometric design is hand-carved, creating a series of sharp angles and planes that catch the light beautifully. It is a bold and functional art piece that provides a stylish home for your fresh produce while serving as a centerpiece of modern design on your kitchen counter or dining table.", "po6", "Pottery", "Vijay Varma", "4.3", "true"});
        updater.accept("Japanese Style Ceramic Teacup", new String[]{"Experience the simple elegance of Japanese tea culture with this handcrafted ceramic teacup. Designed to be held comfortably in both hands, its simple form and subtle glaze invite a moment of mindfulness and appreciation for the beverage. Each cup is unique, reflecting the wabi-sabi philosophy of finding beauty in imperfection and the natural qualities of the material and clay.", "po7", "Pottery", "Siddhant Chaturvedi", "4.5", "true"});
        updater.accept("Flat-Backed Clay Wall Planter", new String[]{"Bring your walls to life with this unique flat-backed clay wall planter. Designed to hang flush against the wall, it is a perfect solution for small spaces or for creating a vertical garden indoors. Each planter is hand-textured and glazed, providing a beautiful and functional home for trailing plants or herbs while adding a touch of handmade art to your apartment walls.", "po8", "Pottery", "Vikrant Massey", "4.2", "true"});
        updater.accept("Hand-Formed Water Pitcher", new String[]{"Serve water or juice with artisanal flair using this beautiful hand-formed ceramic pitcher. Its organic shape and ergonomic handle make it easy to pour, while the rustic glazed finish adds a touch of handmade warmth to your table. This pitcher is a perfect blend of utility and artistic expression, making every meal feel like a special occasion with its unique and charming presence.", "po9", "Pottery", "Jaideep Ahlawat", "4.6", "true"});
        updater.accept("Set of 4 Hand-Painted Plates", new String[]{"Elevate your dessert course with this set of four hand-painted ceramic plates. Each plate features a unique, delicate design inspired by nature, meticulously rendered with a fine brush. They are not only functional for serving treats but also beautiful enough to be displayed as a wall art collection, showcasing a high level of artistic detail and handmade quality in every stroke.", "po10", "Pottery", "Pratik Gandhi", "4.4", "true"});

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
                .filter(p -> p.getId() != null && !p.getId().equals(excludeId))
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
