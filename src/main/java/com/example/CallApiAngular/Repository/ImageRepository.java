package com.example.CallApiAngular.Repository;

import com.example.CallApiAngular.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

}
