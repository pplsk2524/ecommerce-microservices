package com.poojitha.order_service.service;

import com.poojitha.order_service.client.ProductClient;
import com.poojitha.order_service.dto.OrderRequest;
import com.poojitha.order_service.dto.OrderResponse;
import com.poojitha.order_service.dto.ProductResponse;
import com.poojitha.order_service.entity.Order;
import com.poojitha.order_service.entity.OrderStatus;
import com.poojitha.order_service.exception.InsufficientStockException;
import com.poojitha.order_service.exception.OrderNotFoundException;
import com.poojitha.order_service.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ProductClient productClient;
    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.productClient = productClient;
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
        ProductResponse product = productClient.getProductById(orderRequest.getProductId());
        if (orderRequest.getQuantity() > product.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product "+orderRequest.getProductId());
        }
        Double totalPrice = product.getPrice()*orderRequest.getQuantity();
        Order order = modelMapper.map(orderRequest, Order.class);
        order.setOrderId(null);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PLACED);
        Order savedOrder=orderRepository.save(order);
        productClient.reduceStock(orderRequest.getProductId(),orderRequest.getQuantity());
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponseList = new ArrayList<>();
        orders.forEach(order -> orderResponseList.add(modelMapper.map(order, OrderResponse.class)));
        return orderResponseList;
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFoundException("Order not found with id "+orderId));
        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        if(OrderStatus.CANCELLED.equals(order.getStatus())){
            throw new IllegalStateException("Cancelled order cannot be updated");
        }
        ProductResponse product = productClient.getProductById(orderRequest.getProductId());
        if (orderRequest.getQuantity() > product.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product "+orderRequest.getProductId());
        }
        Double totalPrice = product.getPrice() * orderRequest.getQuantity();
        order.setQuantity(orderRequest.getQuantity());
        //order.setStatus(orderRequest.getStatus());
        order.setProductId(orderRequest.getProductId());
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        productClient.reduceStock(orderRequest.getProductId(),orderRequest.getQuantity());
        return modelMapper.map(order, OrderResponse.class);
    }

//    public String deleteOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
//        orderRepository.delete(order);
//
//        return "Order has been deleted successfully";
//    }

    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        if(OrderStatus.CANCELLED.equals(order.getStatus())){
            throw new IllegalStateException("Order is already cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder=orderRepository.save(order);
        productClient.restoreStock(order.getProductId(),order.getQuantity());
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }
}
