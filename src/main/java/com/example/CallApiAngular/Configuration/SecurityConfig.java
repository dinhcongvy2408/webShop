package com.example.CallApiAngular.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
    // Các endpoint công khai - không cần xác thực
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/log-in",
            "/api/auth/register",
            "/api/auth/introspect",
            "/api/products",
            "/api/products/{id}",
//            "/api/products/paginated",
            "api/products",
            "api/options",
            "api/groups",
            "api/banners",
            "api/images",
            "api/auth/token",
            "images/**",
    };

    @Value("${JWT_SIGNER_KEY:default-key}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> {
                })
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        // Quyền ADMIN
                        .requestMatchers(
                                "/api/users/**","/api/products/paginated")
                        .hasAuthority("SCOPE_ADMIN")

                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        log.info("JWT Signer Key used: {}", signerKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object scope = jwt.getClaims().get("scope");
            if (scope == null) {
                return new ArrayList<>();
            }

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if (scope instanceof String) {
                Arrays.stream(((String) scope).split(" "))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            } else if (scope instanceof List) {
                ((List<String>) scope).stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }

            return authorities;
        });
        return converter;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
