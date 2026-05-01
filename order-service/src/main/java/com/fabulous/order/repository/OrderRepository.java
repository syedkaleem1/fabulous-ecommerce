package com.fabulous.order.repository;

import com.fabulous.order.model.OrderStatus;
import com.fabulous.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
