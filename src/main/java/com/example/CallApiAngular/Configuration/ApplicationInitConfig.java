package com.example.CallApiAngular.Configuration;

import com.example.CallApiAngular.Repository.UserRepository;
import com.example.CallApiAngular.enums.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import  com.example.CallApiAngular.entity.Users;


import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                var roles =new HashSet<String>();
                roles.add(Role.ADMIN.name());
                Users user = Users.builder()
                        .username("admin")
                        .email("admin2408@gmail.com")
                        .password(passwordEncoder.encode("admin12345"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin");
            }
        };
    }
}
