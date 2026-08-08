package com.poojitha.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    private String description;
    @Positive(message = "Price must be greater than 0")
    private Double price;
    @Min(value = 1,message="quantity should be atleast 1")
    @Max(value = 10, message= "quantity cannot exceed 10")
    private Integer quantity;
}
