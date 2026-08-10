package com.poojitha.order_service.dto;


import com.poojitha.order_service.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private OrderStatus status;
}
