package com.example.CallApiAngular.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    Long id;
    String userName;
    String token;
    boolean authenticated;
}
