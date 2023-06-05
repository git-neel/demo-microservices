package com.microservices.java.neel.orderservice.controller;

import com.microservices.java.neel.orderservice.dto.OrderRequestDTO;
import com.microservices.java.neel.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallBackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return orderService.placeOrder(orderRequest);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> placeOrderFallBackMethod(OrderRequestDTO orderRequest, RuntimeException ex) throws URISyntaxException {
        log.info("Inside fallback method");
        return CompletableFuture.supplyAsync(() -> "Inventory service is down, please try again after some time");

    }
}
