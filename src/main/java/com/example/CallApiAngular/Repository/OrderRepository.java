package com.example.CallApiAngular.Repository;

import com.example.CallApiAngular.entity.Order;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(Users user);

    Order findTopByUserOrderByCreatedAtDesc(Users user);

    Optional<Order> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}