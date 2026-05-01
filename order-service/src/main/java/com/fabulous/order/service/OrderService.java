package com.fabulous.order.service;


import com.fabulous.order.dto.request.CreateOrderRequest;
import com.fabulous.order.model.Order;
import com.fabulous.order.model.OrderItem;
import com.fabulous.order.model.OrderStatus;
import com.fabulous.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createPending(CreateOrderRequest req, String userEmail) {
        Order order = new Order();
        order.setOrderNumber("SF-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setUserEmail(userEmail);
        order.setStatus(OrderStatus.PENDING);

        var addr = req.shippingAddress();
        order.setShippingStreet(addr.street());
        order.setShippingCity(addr.city());
        order.setShippingState(addr.state());
        order.setShippingZip(addr.zipCode());
        order.setShippingCountry(addr.country());

        List<OrderItem> items = new ArrayList<>();
        for (var r : req.items()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(r.productId());
            item.setProductName(r.productName());
            item.setQuantity(r.quantity());
            item.setUnitPrice(r.unitPrice());
            item.setSubtotal(r.unitPrice().multiply(BigDecimal.valueOf(r.quantity())));
            items.add(item);
        }
        order.setItems(items);
        order.setTotalAmount(items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return orderRepository.save(order);
    }

    @Transactional
    public Order confirm(Long id, String paymentIntentId, String chargeId) {
        Order order = findById(id);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentIntentId(paymentIntentId);
        order.setPaymentChargeId(chargeId);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long id) {
        Order order = findById(id);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Order> findByUser(String email, Pageable pageable) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
