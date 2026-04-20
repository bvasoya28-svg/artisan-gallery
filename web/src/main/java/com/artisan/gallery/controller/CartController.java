package com.artisan.gallery.controller;

import com.artisan.gallery.model.OrderRecord;
import com.artisan.gallery.model.Product;
import com.artisan.gallery.repository.OrderRepository;
import com.artisan.gallery.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @SuppressWarnings("unchecked")
    private List<Product> getCart(HttpSession session) {
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @Autowired
    private com.artisan.gallery.service.UserService userService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        com.artisan.gallery.model.User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        List<Product> cart = getCart(session);
        double total = cart.stream().mapToDouble(Product::getPrice).sum();
        
        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        if (session.getAttribute("loggedIn") == null) return "redirect:/login";
        Product product = productService.getProductById(productId);
        if (product != null) {
            getCart(session).add(product);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam int index, HttpSession session) {
        List<Product> cart = getCart(session);
        if (index >= 0 && index < cart.size()) {
            cart.remove(index);
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        String userEmail = (String) session.getAttribute("userEmail");
        List<Product> cart = getCart(session);

        if (cart.isEmpty()) return "redirect:/cart";

        for (Product item : cart) {
            OrderRecord order = new OrderRecord(
                userName, 
                userEmail, 
                item.getName(), 
                item.getPrice(), 
                LocalDateTime.now(), 
                "Default Address", 
                "COD", 
                "PLACED", 
                item.getImageUrl()
            );
            orderRepository.save(order);
        }

        cart.clear();
        return "redirect:/account/orders?success";
    }
}
