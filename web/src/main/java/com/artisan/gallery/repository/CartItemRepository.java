package com.artisan.gallery.repository;

import com.artisan.gallery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
    void deleteByUserEmailAndProductId(String userEmail, Long productId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM CartItem c WHERE c.product.id IN :productIds")
    void deleteByProductIds(java.util.List<Long> productIds);
}
