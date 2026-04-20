package com.artisan.gallery.controller;

import com.artisan.gallery.model.Product;
import com.artisan.gallery.model.User;
import com.artisan.gallery.service.ProductService;
import com.artisan.gallery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/sell")
public class SellerController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CloudinaryService cloudinaryService;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping
    public String sellPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        com.artisan.gallery.model.User user;
        
        if (email == null) {
            user = new com.artisan.gallery.model.User("Guest", "guest@example.com", "", "", "Guest User", "v1.jpg", false);
        } else {
            user = userService.getUserByEmail(email);
            if (user == null) {
                user = new com.artisan.gallery.model.User("Guest", "guest@example.com", "", "", "Guest User", "v1.jpg", false);
            }
        }

        model.addAttribute("user", user);
        if (user.isSellerTermsAccepted()) {
            return "redirect:/sell/upload";
        }
        return "agreement";
    }

    @PostMapping("/agree")
    public String acceptTerms(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        userService.acceptSellerTerms(email);
        return "redirect:/sell/upload";
    }

    @GetMapping("/upload")
    public String uploadPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";

        User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("myProducts", productService.getUserItems(email));
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam String name, @RequestParam String category,
                               @RequestParam String description, @RequestParam Double price,
                               @RequestParam(defaultValue = "true") boolean inStock,
                               @RequestParam("image") MultipartFile file,
                               HttpSession session, Model model) throws IOException {
        
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";

        User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        if (user.getLifetimeUploadCount() >= 15) {
            model.addAttribute("error", "You have reached the maximum limit of 15 lifetime uploads.");
            model.addAttribute("user", user);
            model.addAttribute("myProducts", productService.getUserItems(email));
            return "upload";
        }

        String imageUrl = "v1.jpg"; // Default
        if (!file.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(file);
        }

        Product product = new Product(name, description, price, imageUrl, category, user.getFullName(), 5.0, 0, "2 Days", email);
        product.setLocationType("National");
        product.setSpecificLocation("India");
        product.setInStock(inStock);

        productService.saveProduct(product);

        // Update lifetime count
        user.setLifetimeUploadCount(user.getLifetimeUploadCount() + 1);
        userService.saveUser(user);

        return "redirect:/sell/upload?success";
    }
}
