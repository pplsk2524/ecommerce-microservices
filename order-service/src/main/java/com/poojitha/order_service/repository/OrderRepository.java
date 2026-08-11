package com.poojitha.order_service.repository;

import com.poojitha.order_service.entity.Order;
import com.poojitha.order_service.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatus(OrderStatus status);
}
