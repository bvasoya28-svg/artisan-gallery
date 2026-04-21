package com.artisan.gallery.repository;

import com.artisan.gallery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
    void deleteByUserEmailAndProductId(String userEmail, Long productId);
}
