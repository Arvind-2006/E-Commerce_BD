package com.app.ecommerce.controller;

import com.app.ecommerce.dto.OrderRequest;
import com.app.ecommerce.model.Order;
import com.app.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody OrderRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Order completedOrder = orderService.placeOrder(request, userDetails.getUsername());
        return ResponseEntity.ok("Order processed successfully! Confirmation Reference Order ID: " + completedOrder.getId());
    }
}