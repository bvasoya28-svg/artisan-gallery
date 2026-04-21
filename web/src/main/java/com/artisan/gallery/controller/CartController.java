package com.artisan.gallery.controller;

import com.artisan.gallery.model.CartItem;
import com.artisan.gallery.model.OrderRecord;
import com.artisan.gallery.model.Product;
import com.artisan.gallery.model.User;
import com.artisan.gallery.repository.CartItemRepository;
import com.artisan.gallery.repository.OrderRepository;
import com.artisan.gallery.service.ProductService;
import com.artisan.gallery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartItemRepository.findByUserEmail(userEmail);
        List<Product> products = cartItems.stream()
                .map(CartItem::getProduct)
                .collect(Collectors.toList());
        
        double total = products.stream().mapToDouble(Product::getPrice).sum();
        
        model.addAttribute("user", user);
        model.addAttribute("cart", products);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) return "redirect:/login";

        Product product = productService.getProductById(productId);
        if (product != null) {
            CartItem item = new CartItem(userEmail, product);
            cartItemRepository.save(item);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail != null) {
            List<CartItem> items = cartItemRepository.findByUserEmail(userEmail);
            for (CartItem item : items) {
                if (item.getProduct().getId().equals(productId)) {
                    cartItemRepository.delete(item);
                    break; // Only remove one instance
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        String userEmail = (String) session.getAttribute("userEmail");
        
        List<CartItem> cartItems = cartItemRepository.findByUserEmail(userEmail);
        if (cartItems.isEmpty()) return "redirect:/cart";

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            OrderRecord order = new OrderRecord(
                userName, 
                userEmail, 
                product.getName(), 
                product.getPrice(), 
                LocalDateTime.now(), 
                "Default Address", 
                "COD", 
                "PLACED", 
                product.getImageUrl()
            );
            orderRepository.save(order);
        }

        // Only clear cart AFTER successful order
        cartItemRepository.deleteAll(cartItems);
        return "redirect:/account/orders?success";
    }
}
