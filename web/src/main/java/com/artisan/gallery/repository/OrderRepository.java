package com.artisan.gallery.repository;

import com.artisan.gallery.model.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
    List<OrderRecord> findByUserEmail(String userEmail);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByUserEmail(String userEmail);
}
