package com.farmtrade.platform.repository;

import com.farmtrade.platform.model.Product;
import com.farmtrade.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByFarmer(User farmer);
    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);
}
