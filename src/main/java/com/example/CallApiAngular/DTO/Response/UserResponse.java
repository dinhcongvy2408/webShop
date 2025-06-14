package com.example.CallApiAngular.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
}
