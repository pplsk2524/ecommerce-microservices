package com.poojitha.order_service.controller;

import com.poojitha.order_service.dto.OrderRequest;
import com.poojitha.order_service.dto.OrderResponse;
import com.poojitha.order_service.entity.OrderStatus;
import com.poojitha.order_service.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest),HttpStatus.CREATED);
    }

//    @GetMapping
//    public ResponseEntity<List<OrderResponse>> getAllOrders() {
//        return new ResponseEntity<>(orderService.getAllOrders(),HttpStatus.OK);
//    }
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders( @RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size){
        return new ResponseEntity<>( orderService.getAllOrders(page,size),HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId),HttpStatus.OK);
    }

//    @GetMapping("/status/{status}")
//    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
//        return new ResponseEntity<>(orderService.getOrdersByStatus(status),HttpStatus.OK);
//    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status, @RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {
        return new ResponseEntity<>(orderService.getOrdersByStatus(status,page,size),HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long orderId,@Valid @RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.updateOrder(orderId,orderRequest),HttpStatus.OK);
    }

//    @DeleteMapping("/{orderId}")
//    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
//        return new ResponseEntity<>(orderService.deleteOrder(orderId),HttpStatus.OK);
//    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.cancelOrder(orderId),HttpStatus.OK);
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.confirmOrder(orderId),HttpStatus.OK);
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.shipOrder(orderId),HttpStatus.OK);
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.deliverOrder(orderId),HttpStatus.OK);
    }
}
