package com.farmtrade.platform.service;

import com.farmtrade.platform.dto.ProductRequest;
import com.farmtrade.platform.model.Product;
import com.farmtrade.platform.model.Role;
import com.farmtrade.platform.model.User;
import com.farmtrade.platform.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    public List<Product> getMyProducts(User farmer) {
        return productRepository.findByFarmer(farmer);
    }

    public Product createProduct(ProductRequest request, User farmer) {
        if (farmer.getRole() != Role.FARMER && farmer.getRole() != Role.ADMIN) {
            throw new SecurityException("Only farmers can list products");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .pricePerUnit(request.getPricePerUnit())
                .unit(request.getUnit())
                .quantityAvailable(request.getQuantityAvailable())
                .farmer(farmer)
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest request, User requester) {
        Product product = getById(id);
        assertOwnerOrAdmin(product, requester);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPricePerUnit(request.getPricePerUnit());
        product.setUnit(request.getUnit());
        product.setQuantityAvailable(request.getQuantityAvailable());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id, User requester) {
        Product product = getById(id);
        assertOwnerOrAdmin(product, requester);
        product.setActive(false);
        productRepository.save(product);
    }

    private void assertOwnerOrAdmin(Product product, User requester) {
        boolean isOwner = product.getFarmer().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("You do not have permission to modify this product");
        }
    }
}
