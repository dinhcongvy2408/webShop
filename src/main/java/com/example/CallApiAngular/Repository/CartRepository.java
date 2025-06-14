package com.example.CallApiAngular.Repository;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(Users user);
}
