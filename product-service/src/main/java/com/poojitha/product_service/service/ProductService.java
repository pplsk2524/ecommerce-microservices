package com.poojitha.product_service.service;

import com.poojitha.product_service.dto.ProductRequest;
import com.poojitha.product_service.dto.ProductResponse;
import com.poojitha.product_service.entity.Product;
import com.poojitha.product_service.exception.ProductNotFoundException;
import com.poojitha.product_service.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    public final ProductRepository productRepository;
    public final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository,ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public List<ProductResponse> getAllProducts() {
//        List<ProductResponse> productResponses = new ArrayList<>();
//        List<Product> products= productRepository.findAll();
//        for(Product product:products){
//            ProductResponse productResponse=modelMapper.map(product,ProductResponse.class);
//            productResponses.add(productResponse);
//        }
//
//        return productResponses;
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product savedProduct = productRepository.save(modelMapper.map(request, Product.class));
        return modelMapper.map(savedProduct, ProductResponse.class);
    }

    public ProductResponse getProductById(Long id) {
        Product product= productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found with id "+id));
        return modelMapper.map(product, ProductResponse.class);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id " + id));
            existing.setName(request.getName());
            existing.setPrice(request.getPrice());
            existing.setQuantity(request.getQuantity());
            existing.setDescription(request.getDescription());
            Product updated =productRepository.save(existing);
            return modelMapper.map(updated, ProductResponse.class);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
        productRepository.delete(product);
    }
}
