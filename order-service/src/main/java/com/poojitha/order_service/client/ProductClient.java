package com.poojitha.order_service.client;

import com.poojitha.order_service.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="product-service")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponse getProductById(@PathVariable Long id);

    @PutMapping("/products/{id}/reduce-stock")
    ProductResponse reduceStock(@PathVariable Long id,@RequestParam Integer quantity);

    @PutMapping("/products/{id}/restore-stock")
    ProductResponse restoreStock(@PathVariable Long id,@RequestParam Integer quantity);
}
