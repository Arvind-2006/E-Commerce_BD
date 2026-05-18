package com.app.ecommerce.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private Integer quantity;
}