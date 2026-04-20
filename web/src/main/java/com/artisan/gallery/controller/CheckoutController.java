package com.artisan.gallery.controller;

import com.artisan.gallery.model.OrderRecord;
import com.artisan.gallery.model.Product;
import com.artisan.gallery.model.User;
import com.artisan.gallery.repository.OrderRepository;
import com.artisan.gallery.service.ProductService;
import com.artisan.gallery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String checkoutFromCart(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null) return "redirect:/login";
        
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        prepareCheckoutModel(session, model, cart);
        return "checkout";
    }

    @GetMapping("/direct")
    public String checkoutDirect(@RequestParam Long productId, HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null) return "redirect:/login";
        
        Product product = productService.getProductById(productId);
        if (product == null) return "redirect:/home";

        List<Product> items = new ArrayList<>();
        items.add(product);
        
        prepareCheckoutModel(session, model, items);
        session.setAttribute("directCheckoutItems", items);
        return "checkout";
    }

    private void prepareCheckoutModel(HttpSession session, Model model, List<Product> items) {
        String email = (String) session.getAttribute("userEmail");
        User user = userService.getUserByEmail(email);
        
        if (user == null) {
            session.invalidate();
            return;
        }

        double total = items.stream().mapToDouble(Product::getPrice).sum();
        
        model.addAttribute("user", user);
        model.addAttribute("checkoutItems", items);
        model.addAttribute("total", total);
    }

    @GetMapping("/buy-now/{id}")
    public String buyNow(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedIn") == null) return "redirect:/login";
        return "redirect:/checkout/direct?productId=" + id;
    }

    @PostMapping("/place-order")
    public String placeOrderPost(@RequestParam String addressOption, 
                               @RequestParam(required = false) String newAddress,
                               @RequestParam String paymentMethod,
                               HttpSession session) {
        
        String email = (String) session.getAttribute("userEmail");
        String userName = (String) session.getAttribute("userName");
        User user = userService.getUserByEmail(email);

        String finalAddress = "default".equals(addressOption) ? user.getAddress() : newAddress;
        if (finalAddress == null || finalAddress.isEmpty()) finalAddress = "No address provided";

        List<Product> items = (List<Product>) session.getAttribute("directCheckoutItems");
        if (items == null) {
            items = (List<Product>) session.getAttribute("cart");
        }

        if (items == null || items.isEmpty()) return "redirect:/home";

        for (Product p : items) {
            OrderRecord order = new OrderRecord(
                userName, email, p.getName(), p.getPrice(), 
                LocalDateTime.now(), finalAddress, paymentMethod, "PLACED", p.getImageUrl()
            );
            orderRepository.save(order);
        }

        if (session.getAttribute("directCheckoutItems") != null) {
            session.removeAttribute("directCheckoutItems");
        } else if (session.getAttribute("cart") != null) {
            ((List<Product>) session.getAttribute("cart")).clear();
        }

        return "redirect:/account/orders?success";
    }
}
