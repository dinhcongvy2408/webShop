package com.example.CallApiAngular.Repository;

import com.example.CallApiAngular.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);
}