package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.DTO.Request.ApiResponse;
import com.example.CallApiAngular.DTO.Response.UserResponse;
import com.example.CallApiAngular.Service.UserService;
import com.example.CallApiAngular.entity.Users;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Log thông tin xác thực (Principal và Authorities)
        log.info("Principal: {}", authentication.getPrincipal());
        log.info("Authorities:");
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            log.info("- {}", authority.getAuthority());
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_ADMIN"));
        log.info("Day la ADMIN: {}", isAdmin);

        ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(Principal principal) {
        // Principal chứa username của người dùng đã xác thực
        String username = principal.getName();

        Users user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        // Lấy các vai trò từ đối tượng Authentication của Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Tạo UserResponse để trả về
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .build();

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder().result(response).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        Optional<Users> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users updatedUser) {
        Optional<Users> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            return ResponseEntity.ok(userService.createUser(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<Users> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
