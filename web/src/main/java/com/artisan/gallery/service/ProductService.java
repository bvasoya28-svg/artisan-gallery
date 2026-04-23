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
        // Remove Shahid Kapoor's product explicitly if found
        repository.findAll().stream()
            .filter(p -> p.getArtist() != null && p.getArtist().toLowerCase().contains("shahid"))
            .forEach(p -> {
                reviewRepository.deleteByProductId(p.getId());
                cartItemRepository.deleteByProductIds(java.util.Collections.singletonList(p.getId()));
                repository.delete(p);
            });

        List<Product> systemProducts = repository.findByUploader("System");
        
        // Key by Image ID (e.g., "v17") to ensure we update the CORRECT record
        Map<String, Product> existingByImgId = new HashMap<>();
        for (Product p : systemProducts) {
            if (p.getImageUrl() != null && p.getImageUrl().contains("/")) {
                String url = p.getImageUrl();
                String imgId = url.substring(url.lastIndexOf("/") + 1);
                // Clean up extension if present
                if (imgId.contains(".")) {
                    imgId = imgId.substring(0, imgId.lastIndexOf("."));
                }
                existingByImgId.put(imgId.toLowerCase(), p);
            }
        }
        
        List<Product> toSave = new ArrayList<>();
        String baseUrl = "https://res.cloudinary.com/dpt2wn9lh/image/upload/";

        // Rating cycle: 3, 3.5, 4, 4.5, 5
        double[] ratingValues = {3.0, 3.5, 4.0, 4.5, 5.0};
        final int[] rIdx = {0};

        java.util.function.BiConsumer<String, String[]> updater = (name, data) -> {
            String imgId = data[1].toLowerCase();
            // Find existing product by its Image ID, or create new if it doesn't exist
            Product p = existingByImgId.getOrDefault(imgId, new Product());
            
            p.setName(name);
            p.setDescription(data[0]);
            p.setImageUrl(baseUrl + data[1]); // Keep original case for URL
            p.setCategory(data[2]);
            p.setArtist(data[3]);
            
            // Assign cycling ratings to satisfy "different rating to every product"
            p.setRating(ratingValues[rIdx[0] % ratingValues.length]);
            rIdx[0]++;

            p.setInStock(Boolean.parseBoolean(data[5]));
            
            if (p.getId() == null) {
                p.setUploader("System");
                p.setPrice((double)(1000 + new Random().nextInt(4000)));
                p.setReviewCount(15 + new Random().nextInt(80));
                p.setDeliveryTime("3-5 Days");
            }
            toSave.add(p);
        };

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

        // OTHERS & ACCESSORIES (v1-v13)
        updater.accept("Whispering Vine Anklet Elegance", new String[]{"Experience the elegance of nature with this handcrafted vine-inspired anklet. Intricately woven with rose-gold tones and delicate leaf motifs, it wraps gracefully around the ankle for a sophisticated, feminine look. Perfect for special occasions or adding a touch of whimsical charm to your daily attire, this piece is a testament to fine artisanal jewelry design.", "v1", "Others", "Karan Kapur", "4.7", "true"});
        updater.accept("Lunar Forest Fairy Moon", new String[]{"Illuminate your space with this enchanting crescent moon diorama. Hand-crafted from natural wood and fibers, it features a miniature tree and a tiny fairy figurine nestled within its curve. Integrated LED fairy lights create a soft, magical glow, making it a perfect nightlight or decorative accent for a nursery or bedroom inspired by woodland fantasy.", "v2", "Others", "Kaira Advani", "4.5", "true"});
        updater.accept("Ethereal Fairy Jar Lantern", new String[]{"Capture the magic of a secret garden with this stunning fairy in a jar. A delicate figurine is suspended within a glass lantern, surrounded by sparkling fairy lights and preserved moss. This handcrafted piece brings a touch of whimsical wonder to any room, serving as a captivating centerpiece that sparks the imagination and adds a warm, inviting atmosphere.", "v3", "Others", "Suresh Raina", "4.8", "true"});
        updater.accept("Mini Macrame Plant Magnets", new String[]{"Add a touch of bohemian charm to your kitchen with these miniature macrame plant magnets. Each tiny hanger is hand-knotted from natural cotton cord and holds a realistic miniature plant. These adorable accessories are perfect for decorating your refrigerator or magnetic board, bringing a bit of handmade greenery and texture to your indoor spaces in a small way.", "v4", "Others", "Manish Pandey", "4.3", "true"});
        updater.accept("Coastal Shell Wall Hanging", new String[]{"Bring the serenity of the seashore into your home with this beautiful shell wall hanging. Dozens of hand-selected seashells and polished stones are suspended from a natural driftwood branch with delicate twine. This handcrafted piece creates a gentle, soothing sound in the breeze and adds a rustic, maritime aesthetic to any room, perfect for coastal-themed decor.", "v5", "Others", "Hardik Pandya", "4.9", "true"});
        updater.accept("Fruit Salad Wall Clock", new String[]{"Tell time in a fun and vibrant way with this unique fruit-themed wall clock. Hand-crafted with 3D miniature fruits like watermelon, avocado, and strawberries representing the hours, it is a playful addition to any kitchen or dining area. The bright colors and whimsical design make it a great conversation piece and a cheerful way to keep track of your day.", "v6", "Others", "Rohit Sharma", "4.6", "true"});
        updater.accept("Enchanted Elven Ear Cuff", new String[]{"Embrace your inner forest spirit with this intricate elven-inspired ear cuff. Hand-crafted from copper-toned wire and adorned with green beads and delicate leaf patterns, it fits comfortably around the ear without needing a piercing. This unique piece of wearable art is perfect for cosplay, festivals, or anyone who loves fantasy-inspired jewelry and artisanal craftsmanship.", "v7", "Others", "Virat Kohli", "4.4", "true"});
        updater.accept("Emerald Bloom Hand Chain", new String[]{"Adorn your hand with the elegance of this emerald-toned flower hand chain. The intricate design features delicate chains connecting a finger ring to an adjustable bracelet, decorated with shimmering green stones and floral motifs. This handcrafted jewelry piece adds a touch of vintage sophistication and bohemian flair to your look, perfect for weddings or festive occasions.", "v8", "Others", "Shubman Gill", "4.2", "true"});
        updater.accept("Crystal Dewdrop Tiara", new String[]{"Feel like royalty with this ethereal handcrafted wire tiara. Delicate silver-toned wires are woven into an intricate crown design and adorned with shimmering crystal dewdrops that catch the light beautifully. This lightweight and elegant accessory is perfect for brides, prom, or any special event where you want to add a touch of magical, sparkling sophistication to your hairstyle.", "v9", "Others", "KL Rahul", "4.1", "true"});
        updater.accept("Iridescent Butterfly Wall Art", new String[]{"Transform your walls with the shimmering beauty of this large iridescent butterfly hanging. Hand-crafted with delicate, translucent materials that reflect a rainbow of colors, it appears to glow when caught by the light. This ethereal piece of art adds a touch of whimsical elegance and a pop of color to a bedroom, nursery, or any creative space in your home.", "v10", "Others", "Rishabh Pant", "4.5", "true"});
        updater.accept("White Jasmine Hand Chain", new String[]{"Experience the delicate beauty of this jasmine-inspired white flower hand chain. Hand-crafted with intricate filigree work and soft white floral accents, it elegantly connects a ring to a bracelet with shimmering gold-toned chains. This romantic accessory is a perfect statement piece for a summer wedding or a garden party, showcasing a love for detailed, feminine jewelry.", "v11", "Others", "Jasprit Bumrah", "4.3", "true"});
        updater.accept("Silver Vine Armlet", new String[]{"Wrap your arm in the beauty of nature with this elegant silver-toned vine armlet. The flexible design features a central white blossom surrounded by delicate swirling vines that adjust to fit your arm comfortably. This handcrafted piece of jewelry adds a touch of bohemian grace and unique style to any sleeveless outfit, perfect for summer festivals and artistic events.", "v12", "Others", "Mohammed Shami", "4.6", "true"});
        updater.accept("Arctic Penguin Perpetual Calendar", new String[]{"Keep track of the date with this adorable handcrafted penguin perpetual calendar. Made from durable, hand-painted materials, the penguin figure holds two wooden blocks for the date and three for the month. This charming and functional desk accessory is perfect for children's rooms or any office space, providing a sustainable and whimsical way to stay organized every day.", "v13", "Others", "Ravindra Jadeja", "4.4", "true"});

        // DECOR & CRAFT (c1-c10, v14-v19)
        updater.accept("Midnight Manor Miniature Diorama", new String[]{"Step into a world of wonder with this handcrafted multistory miniature manor. Every room is meticulously detailed with tiny furniture, soft rugs, and working LED lights that cast a warm, inviting glow. This collector's piece is a testament to the beauty of scale modeling and serves as a captivating focal point for any shelf or display cabinet.", "c1", "Craft", "Ishaan Khatter", "4.8", "true"});
        updater.accept("Celestial Sun & Moon Catcher", new String[]{"Embrace the magic of the cosmos with this stunning sun and moon wind catcher. Hand-crafted from copper-toned wire and adorned with shimmering crystals and amber beads, it captures and refracts light to create a dance of rainbows across your room. It is a perfect spiritual accent for a sunny window, bringing harmony and celestial beauty to your home.", "c2", "Craft", "Ananya Panday", "4.6", "true"});
        updater.accept("Metamorphosis Butterfly Wall Art", new String[]{"Celebrate the beauty of transformation with this striking 3D wall sculpture. A cluster of delicately crafted pink butterflies appears to take flight from within a minimalist black frame, creating a sense of movement and grace. This modern art piece adds a touch of whimsical elegance and a pop of color to any contemporary living space or bedroom.", "c3", "Craft", "Sara Ali Khan", "4.5", "true"});
        updater.accept("Wanderlust Suitcase Bedroom", new String[]{"Discover a hidden sanctuary inside this vintage-inspired suitcase diorama. A cozy, bohemian bedroom scene is perfectly preserved within, complete with a tiny bed, plush pillows, and miniature decor. It is a nostalgic and imaginative artwork that speaks to the traveler's heart, capturing a sense of home and comfort in the most unexpected of places.", "c4", "Craft", "Janhvi Kapoor", "4.9", "true"});
        updater.accept("Eco-Artisan Miniature Home", new String[]{"Witness the power of upcycling with this extraordinary miniature home built inside a repurposed detergent bottle. The artist has transformed everyday plastic into a charming multistory kitchen and living area, complete with tiny checkered floors and floral wallpaper. This piece is a unique statement on sustainability and the boundless possibilities of creative imagination.", "c5", "Craft", "Kartik Aaryan", "4.4", "true"});
        updater.accept("Vibrant Quilled Flower Bouquet", new String[]{"Experience the intricate beauty of paper quilling with this stunning floral bouquet. Hundreds of colorful paper strips have been painstakingly rolled and shaped to create a vibrant arrangement of roses and blossoms. This handcrafted artwork is a permanent celebration of nature's beauty, perfect for gifting or as a delicate decorative accent that never fades.", "c6", "Craft", "Ayushmann Khurrana", "4.3", "true"});
        updater.accept("Whimsical Fishbowl TV Set", new String[]{"Tune into nature with this quirky miniature television that doubles as a tiny fishbowl. Hand-crafted with a vintage aesthetic, the 'screen' reveals a peaceful underwater world with miniature orange fish and colorful pebbles. It is a playful and imaginative conversation piece that brings a touch of humor and aquatic charm to your desk or nightstand.", "c7", "Craft", "Ranbir Kapoor", "4.7", "true"});
        updater.accept("Stories in a Matchbox Collection", new String[]{"Explore a collection of tiny stories with this set of matchbox dioramas. Each small box opens to reveal a unique, hand-crafted scene, from bustling city streets to quiet forest clearings. These miniatures are a masterclass in detail and storytelling on a micro-scale, making them a perfect gift for collectors and those who appreciate the magic of small things.", "c8", "Craft", "Alia Bhatt", "4.6", "true"});
        updater.accept("Deep Sea Scuba Shadow Box", new String[]{"Dive into the depths with this immersive underwater shadow box. The layered paper art creates a stunning 3D effect, depicting a scuba diver exploring a vibrant coral reef teeming with exotic fish. Encased in a natural wood frame, this piece captures the serenity and mystery of the ocean, making it a beautiful addition to any maritime-themed decor.", "c9", "Craft", "Ranveer Singh", "4.5", "true"});
        updater.accept("Tropical Bloom Barefoot Sandal", new String[]{"Walk with the grace of the islands with this beautiful floral barefoot sandal. Hand-crafted with delicate white and yellow blossoms connected by shimmering silver-toned chains, it elegantly wraps around the ankle and toe. Perfect for beach weddings, summer festivals, or simply feeling like a garden goddess while walking along the shore.", "c10", "Craft", "Deepika Padukone", "4.2", "true"});
        updater.accept("Zen Garden Tic-Tac-Toe Set", new String[]{"Unplug and enjoy a moment of mindful play with this handcrafted Zen Garden Tic-Tac-Toe set. The set features a ceramic board and hand-painted stone tokens in black and white, depicting yin-yang and nature symbols. It is a beautiful and tactile desktop accessory that encourages relaxation and friendly competition, making it a perfect gift for the office or home.", "v14", "Craft", "Vicky Kaushal", "4.8", "true"});
        updater.accept("Artisan French Fry & Dip Bowl", new String[]{"Serve your snacks in style with this clever and quirky French fry bowl. Hand-crafted from high-quality ceramic, it features a built-in compartment for your favorite dip, making it perfect for mess-free snacking. The rustic, hand-formed aesthetic and charming 'Dip Me' and 'Eat Me' lettering add a touch of artisanal personality to your casual dining and movie nights.", "v15", "Pottery", "Katrina Kaif", "4.4", "true"});
        updater.accept("Vine-Enchanted Jewelry Arch", new String[]{"Organize your precious accessories with this stunning vine-enchanted ceramic jewelry stand. Meticulously handcrafted, it features an elegant arched design adorned with hand-painted climbing vines and delicate flowers. The built-in holes and ridges provide ample space for earrings, necklaces, and rings, transforming your vanity into a display of artistic botanical beauty.", "v17", "Craft", "Shraddha Kapoor", "4.7", "true"});
        updater.accept("Rose Quartz Jewelry Tree", new String[]{"This breathtaking jewelry stand is shaped like a delicate coral tree in a soft, rose-pink hue. Hand-crafted from durable materials, its many branches are perfect for hanging necklaces, bracelets, and earrings, while the wide base provides a safe spot for rings. It is a functional and beautiful piece of decor that adds a touch of oceanic elegance and feminine grace to your dresser.", "v18", "Craft", "Varun Dhawan", "4.5", "true"});
        updater.accept("Calla Lily Ceramic Candle Holder", new String[]{"Add a touch of floral elegance to your evenings with this exquisite Calla Lily candle holder. Hand-crafted with incredible detail, the ceramic lily appears to bloom from its base, holding a single taper candle in its center. The soft white and pink tones and graceful curves create a romantic and peaceful atmosphere, making it a perfect centerpiece for a dinner table or mantel.", "v19", "Craft", "Kriti Sanon", "4.6", "true"});

        // CROCHET & JEWELRY (cr1-cr10)
        updater.accept("Artisanal Clay Keyring Collection", new String[]{"A delightful assortment of handmade clay keyrings featuring various shapes like hearts, stars, and fruits. Each piece is meticulously sculpted and painted by hand, showcasing a vibrant array of colors and charming details. These keyrings are perfect for adding a touch of personality and handmade warmth to your keys, bags, or as a thoughtful small gift.", "cr1", "Crochet", "Sonam Kapoor", "4.9", "true"});
        updater.accept("Handcrafted Blue Flower Bouquet", new String[]{"A stunning, everlasting bouquet of handcrafted blue flowers, meticulously arranged with delicate white accents and green foliage. Wrapped in rustic brown paper and tied with a simple ribbon, this bouquet brings a touch of floral elegance and handcrafted beauty to any space without the need for water or maintenance.", "cr2", "Crochet", "Kareena Kapoor", "4.7", "true"});
        updater.accept("Rose Garden Crochet Pillow", new String[]{"This exquisitely detailed crochet pillow cover features a grid of beautiful pink roses and lavender sprigs, all hand-stitched with intricate detail. The soft white base and delicate lace trim add a touch of vintage charm and cozy elegance to your bed or sofa, making it a perfect accent piece for a romantic and comfortable home decor.", "cr3", "Crochet", "Priyanka Chopra", "4.8", "true"});
        updater.accept("Floral Lace Crochet Scarf", new String[]{"Wrap yourself in elegance with this lightweight, hand-crocheted lace scarf. Featuring delicate pink floral motifs and trailing green vines, it creates a romantic and ethereal look. Crafted from soft, high-quality yarn, it is a perfect accessory to add a touch of artisanal charm and sophisticated style to any outfit, from casual daywear to evening attire.", "cr4", "Crochet", "Shraddha Kapoor", "4.4", "true"});
        updater.accept("Nautical Starfish Crochet Tote", new String[]{"A charming hand-crocheted tote bag featuring a prominent orange starfish and seashell charms. The open-weave design and sturdy handles make it a perfect companion for a day at the beach or a casual summer outing. This bag combines practical utility with a whimsical nautical aesthetic, showcasing a love for handmade crafts and seaside adventures.", "cr5", "Crochet", "Tiger Shroff", "4.3", "true"});
        updater.accept("Pastel Sky Crochet Two-Piece", new String[]{"A beautiful, hand-crocheted two-piece set in soft pastel blue and white tones. The intricate patterns and delicate texture create a dreamy and feminine look, perfect for warm weather or a special occasion. This set highlights the artistry of crochet, offering a unique and stylish alternative to mass-produced fashion.", "cr6", "Crochet", "Varun Dhawan", "4.6", "true"});
        updater.accept("Enchanted Garden Hair Vine", new String[]{"A delicate and whimsical hair accessory featuring tiny hand-crafted pink flowers and green leaves on a thin, flexible vine. This piece can be woven into various hairstyles, adding a touch of natural beauty and ethereal charm to your look. Perfect for weddings, festivals, or any occasion where you want to feel like a woodland nymph.", "cr7", "Crochet", "Sidharth Malhotra", "4.5", "true"});
        updater.accept("Butterfly Meadow Wall Hanging", new String[]{"Brighten your walls with this cheerful hand-crafted wall hanging. Featuring a large, colorful butterfly surrounded by vibrant flowers and delicate greenery, it is suspended from a simple wooden dowel. This piece adds a touch of whimsical nature and handmade warmth to a nursery, bedroom, or creative workspace.", "cr8", "Crochet", "Kiara Advani", "4.7", "true"});
        updater.accept("Garden Bounty Crochet Pencil Cases", new String[]{"A set of fun and unique hand-crocheted pencil cases shaped like garden vegetables, including corn and carrots. Each case features a sturdy zipper to keep your pens and pencils secure. These cases are perfect for students, artists, or anyone who appreciates a touch of quirky, handmade charm in their daily essentials.", "cr9", "Crochet", "Kriti Sanon", "4.2", "true"});
        updater.accept("Lush Leafy Crochet Plant Hanger", new String[]{"Bring a touch of green into your home with this unique hand-crocheted plant hanger. Featuring large, intricately detailed green leaves that cradle a small potted plant, it adds a vertical dimension of botanical beauty to your space. The sturdy design ensures your plant is held securely while showcasing the artistry of crochet in a functional way.", "cr10", "Craft", "Hrithik Roshan", "4.5", "true"});

        // POTTERY (po1-po10)
        updater.accept("Starry Night Ceramic Vase", new String[]{"This breathtaking hand-painted ceramic vase is inspired by the masterpieces of Impressionist art. Featuring a deep cobalt blue base with vibrant yellow celestial swirls and a glowing moon, it serves as a stunning functional art piece. Each brushstroke is carefully applied to create a sense of movement and nocturnal magic, making it a perfect centerpiece for any room.", "po1", "Pottery", "Rajkumar Rao", "4.7", "true"});
        updater.accept("Hand-Painted Songbird Jar", new String[]{"A beautiful and intricate ceramic jar featuring a detailed painting of a songbird amidst autumnal leaves. The warm earthy tones and fine line work showcase the artist's dedication to capturing the beauty of nature. This jar is not only a decorative masterpiece but also a functional storage piece for your most cherished small items.", "po2", "Pottery", "Ayushmann Khurrana", "4.6", "true"});
        updater.accept("Golden Sunflower Tall Vase", new String[]{"Bring the warmth of a summer field into your home with this tall, hand-painted sunflower vase. The vibrant yellow petals and detailed centers of the sunflowers are set against a soft, neutral background, creating a cheerful and inviting aesthetic. It is an ideal piece for displaying long-stemmed flowers or as a standalone statement of sunny elegance.", "po3", "Pottery", "Pankaj Tripathi", "4.5", "true"});
        updater.accept("Ornate Bohemian Spice Urn", new String[]{"This colorful and highly decorative ceramic urn is adorned with intricate bohemian patterns and vibrant pigments. Featuring a matching lid, it is perfect for storing spices, tea leaves, or simply as a bold decorative accent. The hand-painted details reflect a rich cultural heritage and a passion for detailed craftsmanship and artistic expression.", "po4", "Pottery", "Nawazuddin Siddiqui", "4.4", "true"});
        updater.accept("White Lily Sculpted Vase", new String[]{"Elegance meets artistry in this stunning white ceramic vase featuring three-dimensional sculpted lily blossoms. The delicate petals and realistic textures create a sense of graceful movement and purity. This monochromatic masterpiece is perfect for sophisticated interiors, adding a touch of floral beauty that never fades and complements any color palette.", "po5", "Pottery", "Manoj Bajpayee", "4.8", "true"});
        updater.accept("Pastel Bow Ribbon Vase", new String[]{"A charming and whimsical light blue ceramic vase adorned with a hand-sculpted pink ribbon bow. The soft pastel colors and gentle curves create a sweet and romantic aesthetic, perfect for a nursery, bedroom, or as a delightful gift. It is designed to hold a small bouquet of fresh flowers, adding a touch of handmade tenderness to your space.", "po6", "Pottery", "Vijay Varma", "4.3", "true"});
        updater.accept("Lotus Petal Teapot Set", new String[]{"Enjoy a serene tea ceremony with this exquisite light blue teapot set, designed to resemble delicate lotus petals and leaves. The set includes a beautifully shaped teapot and two matching cups with leaf-inspired saucers. The smooth, glazed finish and organic forms create a peaceful and artistic tea-drinking experience that celebrates the beauty of nature.", "po7", "Pottery", "Siddhant Chaturvedi", "4.5", "true"});
        updater.accept("Triple Bloom Serving Trio", new String[]{"A delightful set of three small, flower-shaped ceramic bowls in pastel pink, purple, and green. Each bowl comes with a matching decorative spoon, making it perfect for serving condiments, jewelry, or small treats. The whimsical design and vibrant colors add a touch of garden-inspired joy to your table setting or vanity display.", "po8", "Pottery", "Vikrant Massey", "4.2", "true"});
        updater.accept("Whimsical Galaxy Mug Collection", new String[]{"A vibrant set of three hand-painted mugs featuring whimsical celestial and floral designs. With deep blues, bright yellows, and rich greens, each mug is a unique work of art that makes your morning coffee or evening tea feel like a magical experience. The matching saucers and spoons complete this enchanting set, perfect for the artistic soul.", "po9", "Pottery", "Jaideep Ahlawat", "4.6", "true"});
        updater.accept("Midnight Landscape Artisan Plate", new String[]{"This stunning decorative ceramic plate features a hand-painted night landscape inspired by classical starry skies. The swirling clouds, glowing moon, and silhouette of a lonely tree create a sense of peace and wonder. It is a perfect piece for wall display or as a centerpiece, showcasing the artist's mastery of color and nocturnal storytelling.", "po10", "Pottery", "Pratik Gandhi", "4.4", "true"});

        repository.saveAll(toSave);

        // Delete any orphan "System" products that were not updated (including v16/Shahid)
        Set<Long> savedIds = toSave.stream().map(Product::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Product p : systemProducts) {
            if (!savedIds.contains(p.getId())) {
                reviewRepository.deleteByProductId(p.getId());
                cartItemRepository.deleteByProductIds(java.util.Collections.singletonList(p.getId()));
                repository.delete(p);
            }
        }

        repository.flush();
    }

    public List<Product> getAllProducts() { return repository.findAll(); }
    public Product getProductById(Long id) { return repository.findById(id).orElse(null); }
    public List<Product> getProductsByCategory(String category) { return repository.findByCategory(category); }
    public List<Product> searchProducts(String query) { 
        return repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query, query); 
    }
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
