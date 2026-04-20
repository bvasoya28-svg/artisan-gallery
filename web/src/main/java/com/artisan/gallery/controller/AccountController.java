package com.artisan.gallery.controller;

import com.artisan.gallery.model.User;
import com.artisan.gallery.model.OrderRecord;
import com.artisan.gallery.repository.OrderRepository;
import com.artisan.gallery.service.UserService;
import com.artisan.gallery.service.ProductService;
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
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String accountHub(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";
        
        com.artisan.gallery.model.User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("uploadCount", user.getLifetimeUploadCount());
        return "account";
    }

    @GetMapping("/orders")
    public String trackOrders(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";
        User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        
        List<OrderRecord> orders = orderRepository.findByUserEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @PostMapping("/orders/cancel")
    public String cancelOrder(@RequestParam Long orderId, HttpSession session) {
        if (session.getAttribute("loggedIn") == null) return "redirect:/login";
        OrderRecord order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getUserEmail().equals(session.getAttribute("userEmail"))) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
        return "redirect:/account/orders?cancelled";
    }

    @PostMapping("/orders/delete")
    public String deleteOrder(@RequestParam Long orderId, HttpSession session) {
        if (session.getAttribute("loggedIn") == null) return "redirect:/login";
        OrderRecord order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getUserEmail().equals(session.getAttribute("userEmail"))) {
            if ("CANCELLED".equals(order.getStatus())) {
                orderRepository.deleteById(orderId);
            }
        }
        return "redirect:/account/orders?deleted";
    }

    @GetMapping("/details")
    public String accountDetails(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";
        User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "account-details";
    }

    @PostMapping("/update-details")
    public String updateDetails(@RequestParam String fullName, @RequestParam String password, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        User user = userService.getUserByEmail(email);
        if (user != null) {
            String newPassword = password.isEmpty() ? user.getPassword() : password;
            userService.registerUser(fullName, email, newPassword);
        }
        return "redirect:/account/details?success";
    }

    @PostMapping("/update-profile-pic")
    public String updateProfilePic(@RequestParam("profilePic") MultipartFile file, HttpSession session) throws IOException {
        String email = (String) session.getAttribute("userEmail");
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/images/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            userService.updateProfilePicture(email, fileName);
        }
        return "redirect:/account/details?success";
    }

    @GetMapping("/address")
    public String addressPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";
        User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("address", user.getAddress());
        return "address";
    }

    @PostMapping("/update-address")
    public String updateAddress(@RequestParam String address, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        userService.updateAddress(email, address);
        return "redirect:/account?addressUpdated";
    }

    @GetMapping("/contact")
    public String contactInfo(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";
        
        com.artisan.gallery.model.User user = userService.getUserByEmail(email);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "contact";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @PostMapping("/delete-permanently")
    public String deleteAccount(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        userService.deleteUser(email);
        session.invalidate();
        return "redirect:/login?deleted";
    }
}
