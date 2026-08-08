package com.poojitha.order_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotNull
    private Long productId;
    @Min(value = 1, message = "quantity should be atleast 1")
    @Max(value = 20, message = "quantity cannot exceed 20")
    private Integer quantity;
    @Positive
    private Double totalPrice;
    @NotNull(message = "Please enter valid status")
    private String status;
}
