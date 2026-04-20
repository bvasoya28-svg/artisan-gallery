package com.example.handmadeproducts.data

object DataSource {
    val categories = listOf(
        Category("All"),
        Category("Ceramics"),
        Category("Paintings"),
        Category("Home Decor"),
        Category("Stationery & Gifts"),
        Category("Clothing & Accessories")
    )

    val products = listOf(
        // Ceramics
        Product(1, "Starry Night Vase", "A beautiful hand-painted ceramic vase with celestial motifs.", 2400.0, "Ceramics", "Bansari Studio", 4.9, "Tomorrow", 0),
        Product(2, "Earthy Pottery Bowl", "Hand-thrown clay bowl with a rustic glaze.", 1250.0, "Ceramics", "Rajesh Kumar", 4.7, "2 Days", 0),
        Product(3, "Blue Pottery Soap Dish", "Traditional Jaipur blue pottery with floral designs.", 1050.0, "Ceramics", "Anita Arts", 4.8, "Tomorrow", 0),
        Product(4, "Terracotta Planter", "Breathable clay planter for your favorite succulents.", 1150.0, "Ceramics", "Rajesh Kumar", 4.5, "Tomorrow", 0),
        Product(5, "Glazed Coffee Mug", "Large ceramic mug with a unique drip glaze.", 1300.0, "Ceramics", "Bansari Studio", 4.9, "Tomorrow", 0),
        Product(6, "Ceramic Wind Chimes", "Delicate ceramic pieces that create a soothing sound.", 1450.0, "Ceramics", "Anita Arts", 4.6, "3 Days", 0),

        // Paintings
        Product(7, "Abstract Ocean Painting", "Large canvas featuring deep blues and crashing waves.", 4500.0, "Paintings", "Anita Arts", 5.0, "Tomorrow", 0),
        Product(8, "Mandala Canvas Art", "Intricate hand-drawn mandala on a black background.", 2800.0, "Paintings", "Rajesh Kumar", 4.9, "Tomorrow", 0),
        Product(9, "Custom Pet Portrait", "Personalized oil painting of your beloved pet.", 4950.0, "Paintings", "Bansari Studio", 5.0, "5 Days", 0),
        Product(10, "Sunset Landscape", "Vibrant watercolor painting of a mountain sunset.", 3200.0, "Paintings", "Anita Arts", 4.8, "Tomorrow", 0),
        Product(11, "Miniature Folk Art", "Detailed traditional painting on a small wooden block.", 1400.0, "Paintings", "Rajesh Kumar", 4.7, "Tomorrow", 0),
        Product(12, "Modern Geometric Art", "Acrylic painting with bold colors and sharp lines.", 3100.0, "Paintings", "Bansari Studio", 4.6, "2 Days", 0),

        // Home Decor
        Product(13, "Moonlit Fairy Lamp", "Frosted glass jar with delicate fairy lights inside.", 1350.0, "Home Decor", "Bansari Studio", 4.9, "Tomorrow", 0),
        Product(14, "Resin Clock", "Handmade wall clock with real dried flowers in resin.", 2650.0, "Home Decor", "Anita Arts", 4.8, "Tomorrow", 0),
        Product(15, "Macrame Wall Hanging", "Large boho-style macrame piece for any room.", 2550.0, "Home Decor", "Rajesh Kumar", 4.7, "2 Days", 0),
        Product(16, "Scented Soy Candle", "Hand-poured lavender and vanilla soy candle.", 1200.0, "Home Decor", "Anita Arts", 4.9, "Tomorrow", 0),
        Product(17, "Embroidered Cushion Cover", "Detailed silk embroidery on a cotton base.", 1400.0, "Home Decor", "Bansari Studio", 4.8, "Tomorrow", 0),
        Product(18, "Wooden Key Holder", "Hand-carved rustic wooden key rack.", 1250.0, "Home Decor", "Rajesh Kumar", 4.6, "Tomorrow", 0),

        // Stationery & Gifts
        Product(19, "Leather Journal", "Hand-stitched leather notebook with handmade paper.", 1450.0, "Stationery & Gifts", "Rajesh Kumar", 5.0, "Tomorrow", 0),
        Product(20, "Bamboo Organizer", "Eco-friendly desk organizer made of sustainable bamboo.", 1300.0, "Stationery & Gifts", "Bansari Studio", 4.7, "2 Days", 0),
        Product(21, "Quilled Cards Set", "Set of 5 greeting cards with intricate paper quilling.", 1180.0, "Stationery & Gifts", "Anita Arts", 4.9, "Tomorrow", 0),
        Product(22, "Calligraphy Pen Set", "Handcrafted wooden pen with multiple nibs.", 1550.0, "Stationery & Gifts", "Rajesh Kumar", 4.8, "3 Days", 0),
        Product(23, "Handmade Paper Box", "Beautifully decorated gift box made of recycled paper.", 1120.0, "Stationery & Gifts", "Anita Arts", 4.6, "Tomorrow", 0),
        Product(24, "Floral Bookmark", "Pressed flower bookmark laminated for durability.", 1080.0, "Stationery & Gifts", "Bansari Studio", 4.9, "Tomorrow", 0),

        // Clothing & Accessories
        Product(25, "Tie-Dye T-shirt", "Unique hand-dyed cotton t-shirt in indigo colors.", 1350.0, "Clothing & Accessories", "Anita Arts", 4.7, "Tomorrow", 0),
        Product(26, "Hand-painted Jute Bag", "Eco-friendly bag with a colorful bird design.", 1280.0, "Clothing & Accessories", "Bansari Studio", 4.8, "Tomorrow", 0),
        Product(27, "Beaded Boho Earrings", "Intricate glass beadwork in a teardrop shape.", 1220.0, "Clothing & Accessories", "Rajesh Kumar", 4.9, "Tomorrow", 0),
        Product(28, "Silk Scarf", "Hand-painted 100% pure silk scarf.", 2600.0, "Clothing & Accessories", "Anita Arts", 5.0, "2 Days", 0),
        Product(29, "Leather Bracelet", "Braided genuine leather bracelet with a silver clasp.", 1150.0, "Clothing & Accessories", "Rajesh Kumar", 4.6, "Tomorrow", 0),
        Product(30, "Knitted Woolen Beanie", "Soft hand-knitted beanie for winters.", 1250.0, "Clothing & Accessories", "Bansari Studio", 4.8, "Tomorrow", 0)
    ) + (31..59).map { i ->
        Product(
            id = i,
            name = "Artisan Piece #$i",
            description = "A unique handcrafted masterpiece by our expert artisans.",
            price = 1000.0 + (i * 67) % 4000,
            category = listOf("Ceramics", "Paintings", "Home Decor", "Stationery & Gifts", "Clothing & Accessories")[i % 5],
            seller = listOf("Anita Arts", "Bansari Studio", "Rajesh Kumar")[i % 3],
            rating = 4.0 + (i % 10) / 10.0,
            deliveryTime = if (i % 2 == 0) "Tomorrow" else "2 Days",
            imageRes = 0
        )
    }
}
