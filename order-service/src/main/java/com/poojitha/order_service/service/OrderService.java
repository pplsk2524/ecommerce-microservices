package com.poojitha.order_service.service;

import com.poojitha.order_service.dto.OrderRequest;
import com.poojitha.order_service.dto.OrderResponse;
import com.poojitha.order_service.entity.Order;
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
    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = modelMapper.map(orderRequest, Order.class);
        order.setOrderId(null);
        Order savedOrder=orderRepository.save(order);
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
        order.setQuantity(orderRequest.getQuantity());
        order.setStatus(orderRequest.getStatus());
        order.setProductId(orderRequest.getProductId());
        order.setTotalPrice(orderRequest.getTotalPrice());
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    public String deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        orderRepository.delete(order);
        return "Order has been deleted successfully";
    }
}
