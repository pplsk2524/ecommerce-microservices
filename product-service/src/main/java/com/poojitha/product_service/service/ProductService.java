package com.poojitha.product_service.service;

import com.poojitha.product_service.entity.Product;
import com.poojitha.product_service.exception.ProductNotFoundException;
import com.poojitha.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    public final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found with id "+id));
    }

    public Product updateProduct(Long id, Product product) {
        Product existing = productRepository.findById(id).orElse(null);
        if(existing == null) {
            throw new ProductNotFoundException("Product not found with id "+id);
        }
        else{
            existing.setName(product.getName());
            existing.setPrice(product.getPrice());
            existing.setQuantity(product.getQuantity());
            existing.setDescription(product.getDescription());
            return productRepository.save(existing);
        }
    }

    public String deleteProduct(Long id) {
        productRepository.deleteById(id);
        return "Product with id "+id+" deleted";
    }
}
