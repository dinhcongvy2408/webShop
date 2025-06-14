package com.example.CallApiAngular.Repository;

import com.example.CallApiAngular.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, String> {
    Option findByName(String name);
}
