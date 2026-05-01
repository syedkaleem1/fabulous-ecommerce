package com.ecommerce.cartservice.repository;

import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus cartStatus);
}
