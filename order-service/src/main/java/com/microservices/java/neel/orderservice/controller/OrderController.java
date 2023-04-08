package com.microservices.java.neel.orderservice.controller;

import com.microservices.java.neel.orderservice.dto.OrderRequestDTO;
import com.microservices.java.neel.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private String placeOrder(@RequestBody OrderRequestDTO orderRequest) throws URISyntaxException {
        orderService.placeOrder(orderRequest);
        return "Order placed successfully";
    }
}
