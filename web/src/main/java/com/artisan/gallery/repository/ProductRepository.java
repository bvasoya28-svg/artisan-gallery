package com.artisan.gallery.repository;

import com.artisan.gallery.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByUploader(String uploader);
    List<Product> findByUploaderNot(String uploader);
    List<Product> findByUploaderNotAndUploaderNot(String u1, String u2);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String description, String category);
    long countByUploader(String uploader);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByUploader(String uploader);
}
