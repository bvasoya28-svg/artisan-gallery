package com.artisan.gallery.controller;

import com.artisan.gallery.model.Product;
import com.artisan.gallery.model.Review;
import com.artisan.gallery.model.User;
import com.artisan.gallery.repository.ReviewRepository;
import com.artisan.gallery.service.CloudinaryService;
import com.artisan.gallery.service.ProductService;
import com.artisan.gallery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userEmail") != null) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(required = false) String category, 
                           @RequestParam(required = false, defaultValue = "") String search,
                           Model model, HttpSession session) {
        
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        } else if (category != null && !category.isEmpty() && !"All".equals(category)) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getSystemProducts();
        }

        List<Product> sharedCreations = productService.getSharedCreations(userEmail);
        List<Product> myItems = productService.getUserItems(userEmail);

        model.addAttribute("user", user);
        model.addAttribute("products", products);
        model.addAttribute("sharedCreations", sharedCreations);
        model.addAttribute("myItems", myItems);
        model.addAttribute("selectedCategory", category != null ? category : "All");
        model.addAttribute("searchQuery", search);
        
        return "home";
    }

    @GetMapping("/category/{name}")
    public String viewCategory(@PathVariable String name, Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";
        
        User user = userService.getUserByEmail(userEmail);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("category", name);
        model.addAttribute("products", productService.getProductsByCategory(name));
        return "category";
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String query, Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";
        
        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("query", query);
        
        List<String> recentSearches = (List<String>) session.getAttribute("recentSearches");
        if (recentSearches == null) {
            recentSearches = new java.util.ArrayList<>();
        }

        if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = query.trim();
            model.addAttribute("results", productService.searchProducts(trimmedQuery));
            
            if (!recentSearches.contains(trimmedQuery)) {
                recentSearches.add(0, trimmedQuery);
                if (recentSearches.size() > 5) recentSearches.remove(5);
                session.setAttribute("recentSearches", recentSearches);
            }
        }
        
        model.addAttribute("recentSearches", recentSearches);
        model.addAttribute("suggestions", List.of("Pottery", "Paintings", "Jewelry", "Home Decor", "Vintage"));
        
        return "search";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/home";
        
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        User user = userService.getUserByEmail(userEmail);
        if (user == null) return "redirect:/login";
        
        model.addAttribute("user", user);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewRepository.findByProductId(id));
        model.addAttribute("suggestions", productService.getSuggestions(product.getCategory(), id));
        return "detail";
    }

    @PostMapping("/product/review")
    public String postReview(@RequestParam Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            @RequestParam(required = false) MultipartFile image,
                            HttpSession session) throws IOException {
        String userName = (String) session.getAttribute("userName");
        String userEmail = (String) session.getAttribute("userEmail");
        
        String imageUrl = "";
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image);
        }

        Review review = new Review(productId, userName, userEmail, rating, comment, imageUrl, LocalDateTime.now());
        reviewRepository.save(review);
        
        return "redirect:/product/" + productId;
    }

    @PostMapping("/product/toggle-stock")
    public String toggleStock(@RequestParam Long id, HttpSession session) {
        Product product = productService.getProductById(id);
        String email = (String) session.getAttribute("userEmail");
        if (product != null && product.getUploader().equals(email)) {
            product.setInStock(!product.isInStock());
            productService.saveProduct(product);
        }
        return "redirect:/sell/upload";
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam Long id, HttpSession session) {
        Product product = productService.getProductById(id);
        String email = (String) session.getAttribute("userEmail");
        if (product != null && product.getUploader().equals(email)) {
            productService.deleteProduct(id);
        }
        return "redirect:/sell/upload";
    }
}
