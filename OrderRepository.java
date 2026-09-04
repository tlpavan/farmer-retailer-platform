package com.farmtrade.platform.repository;

import com.farmtrade.platform.model.Order;
import com.farmtrade.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRetailer(User retailer);
    List<Order> findByProduct_Farmer(User farmer);
}
