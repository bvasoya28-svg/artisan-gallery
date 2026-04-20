package com.artisan.gallery.service;

import com.artisan.gallery.model.User;
import com.artisan.gallery.repository.UserRepository;
import com.artisan.gallery.repository.ProductRepository;
import com.artisan.gallery.repository.ReviewRepository;
import com.artisan.gallery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public User registerUser(String fullName, String email, String password) {
        Optional<User> existing = userRepository.findByEmail(email);
        User user;
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setUsername(email); 
            user.setAddress(""); 
            user.setSellerTermsAccepted(false);
            user.setProfilePicture("v1.jpg");
        }
        user.setFullName(fullName);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public User login(String identifier, String password) {
        String id = identifier.trim().toLowerCase();
        // Search by email OR username
        Optional<User> userOpt = userRepository.findByEmail(id);
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByUsername(id);
        }

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt.get();
        }
        return null;
    }

    public void updateAddress(String email, String address) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setAddress(address);
            userRepository.save(user);
        });
    }

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(currentPassword)) {
            userOpt.get().setPassword(newPassword);
            userRepository.save(userOpt.get());
            return true;
        }
        return false;
    }

    public void acceptSellerTerms(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setSellerTermsAccepted(true);
            userRepository.save(user);
        });
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void updateProfilePicture(String email, String fileName) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setProfilePicture(fileName);
            userRepository.save(user);
        });
    }

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void deleteUser(String email) {
        orderRepository.deleteByUserEmail(email);
        reviewRepository.deleteByUserEmail(email);
        productRepository.deleteByUploader(email);
        userRepository.findByEmail(email).ifPresent(user -> {
            userRepository.delete(user);
        });
    }
}
