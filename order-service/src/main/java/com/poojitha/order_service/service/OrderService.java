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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
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

//    public List<OrderResponse> getAllOrders() {
//        List<Order> orders = orderRepository.findAll();
//        List<OrderResponse> orderResponseList = new ArrayList<>();
//        orders.forEach(order -> orderResponseList.add(modelMapper.map(order, OrderResponse.class)));
//        return orderResponseList;
//    }

    public Page<OrderResponse> getAllOrders(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).map(order -> modelMapper.map(order, OrderResponse.class));
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFoundException("Order not found with id "+orderId));
        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        if(OrderStatus.CANCELLED.equals(order.getStatus()) || OrderStatus.SHIPPED.equals(order.getStatus()) || OrderStatus.DELIVERED.equals(order.getStatus())){
            throw new IllegalStateException("Shipped, Delivered or Cancelled orders cannot be updated");
        }
        if(!order.getProductId().equals(orderRequest.getProductId())){
            throw new IllegalStateException("Product cannot be changed for an existing order");
        }
        ProductResponse product = productClient.getProductById(orderRequest.getProductId());
        int quantityDifference = orderRequest.getQuantity() - order.getQuantity();
        Double totalPrice = product.getPrice() * orderRequest.getQuantity();

        if(quantityDifference>0){
            if(quantityDifference>product.getQuantity()){
                throw new InsufficientStockException("Insufficient stock for product "+orderRequest.getProductId());
            }
            productClient.reduceStock(orderRequest.getProductId(),quantityDifference);
        }
        else if(quantityDifference<0){
            productClient.restoreStock(orderRequest.getProductId(),Math.abs(quantityDifference));
        }
        order.setQuantity(orderRequest.getQuantity());
        //order.setStatus(orderRequest.getStatus());
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
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
        if(OrderStatus.SHIPPED.equals(order.getStatus()) ||  OrderStatus.DELIVERED.equals(order.getStatus())){
            throw new IllegalStateException("Shipped or delivered orders cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder=orderRepository.save(order);
        productClient.restoreStock(order.getProductId(),order.getQuantity());
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }

    public OrderResponse confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        if(OrderStatus.PLACED.equals(order.getStatus())){
            order.setStatus(OrderStatus.CONFIRMED);
        }
        else{
            throw new IllegalStateException("Only placed orders can be confirmed");
        }
        Order updatedOrder=orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }

    public OrderResponse shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        if(OrderStatus.CONFIRMED.equals(order.getStatus())){
            order.setStatus(OrderStatus.SHIPPED);
        }
        else{
            throw new IllegalStateException("Only confirmed orders can be shipped");
        }
        Order updatedOrder=orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }

    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id "+orderId));
        if(OrderStatus.SHIPPED.equals(order.getStatus())){
            order.setStatus(OrderStatus.DELIVERED);
        }
        else{
            throw new IllegalStateException("Only shipped orders can be delivered");
        }
        Order updatedOrder=orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }

//    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
//        return orderRepository.findByStatus(status).stream().map(order-> modelMapper.map(order,OrderResponse.class)).toList();
//    }

    public Page<OrderResponse> getOrdersByStatus(OrderStatus status,int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return orderRepository.findByStatus(status,pageable).map(order-> modelMapper.map(order,OrderResponse.class));
    }
}
