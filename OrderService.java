package com.farmtrade.platform.service;

import com.farmtrade.platform.dto.OrderRequest;
import com.farmtrade.platform.model.*;
import com.farmtrade.platform.repository.OrderRepository;
import com.farmtrade.platform.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Order placeOrder(OrderRequest request, User retailer) {
        if (retailer.getRole() != Role.RETAILER && retailer.getRole() != Role.ADMIN) {
            throw new SecurityException("Only retailers can place orders");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isActive()) {
            throw new IllegalStateException("This product is no longer available");
        }
        if (product.getQuantityAvailable() < request.getQuantity()) {
            throw new IllegalStateException("Requested quantity exceeds available stock");
        }

        product.setQuantityAvailable(product.getQuantityAvailable() - request.getQuantity());
        productRepository.save(product);

        BigDecimal totalPrice = product.getPricePerUnit().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .product(product)
                .retailer(retailer)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }

    public List<Order> getMyOrders(User retailer) {
        return orderRepository.findByRetailer(retailer);
    }

    public List<Order> getIncomingOrdersForFarmer(User farmer) {
        return orderRepository.findByProduct_Farmer(farmer);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus, User requester) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        boolean isFarmerOwner = order.getProduct().getFarmer().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;

        if (!isFarmerOwner && !isAdmin) {
            throw new SecurityException("Only the farmer who owns this product or an admin can update order status");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
