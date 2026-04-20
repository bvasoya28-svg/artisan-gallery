package com.artisan.gallery.controller;

import com.artisan.gallery.model.User;
import com.artisan.gallery.service.UserService;
import com.artisan.gallery.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String identifier, @RequestParam String password, 
                        HttpSession session, Model model) {
        User user = userService.login(identifier, password);
        if (user != null) {
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("loggedIn", true);
            return "redirect:/home";
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String sendOtp(@RequestParam String fullName, @RequestParam String email, 
                          @RequestParam String password, HttpSession session, Model model) {
        System.out.println("DEBUG: Registration attempt for: " + email);
        if (userService.getUserByEmail(email) != null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        System.out.println("DEBUG: Generated OTP: " + otp);
        
        // Store data in session temporarily
        session.setAttribute("tempName", fullName);
        session.setAttribute("tempEmail", email);
        session.setAttribute("tempPass", password);
        session.setAttribute("sentOtp", otp);

        try {
            // Send Email
            System.out.println("DEBUG: Sending email...");
            emailService.sendOtpEmail(email, otp);
            System.out.println("DEBUG: Email sent successfully");
            
            model.addAttribute("otpSent", true);
            model.addAttribute("email", email);
            return "register";
        } catch (Exception e) {
            System.err.println("DEBUG: EMAIL FAILED: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Email service error. Check the console or use code: " + otp);
            model.addAttribute("otpSent", true); // Still show OTP box so you can use the console code
            model.addAttribute("email", email);
            return "register";
        }
    }

    @PostMapping("/register/verify")
    public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
        String sentOtp = (String) session.getAttribute("sentOtp");
        String fullName = (String) session.getAttribute("tempName");
        String email = (String) session.getAttribute("tempEmail");
        String password = (String) session.getAttribute("tempPass");

        if (sentOtp != null && sentOtp.equals(otp)) {
            userService.registerUser(fullName, email, password);
            session.removeAttribute("sentOtp");
            return "redirect:/login?registered";
        }

        model.addAttribute("error", "Invalid OTP. Please try again.");
        model.addAttribute("otpSent", true);
        model.addAttribute("email", email);
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
