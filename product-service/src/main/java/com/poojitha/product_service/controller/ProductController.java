package com.poojitha.product_service.controller;

import com.poojitha.product_service.dto.ProductRequest;
import com.poojitha.product_service.dto.ProductResponse;
import com.poojitha.product_service.entity.Product;
import com.poojitha.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    public final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request),HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return new ResponseEntity<>(productService.getProductById(id),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest request){
        return new ResponseEntity<>(productService.updateProduct(id,request),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
    }
}
