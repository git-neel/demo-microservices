package com.microservices.java.neel.orderservice.repository;

import com.microservices.java.neel.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
