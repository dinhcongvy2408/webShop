package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.Repository.UserRepository;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.CallApiAngular.DTO.Response.UserResponse;
import java.util.stream.Collectors;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private Users request;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build()).collect(Collectors.toList());
    }

    public Optional<Users> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<Users> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Users createUser(Users users) {

        Users user = new Users();
        user.setUsername(users.getUsername());
        user.setPassword(passwordEncoder.encode(users.getPassword()));
        user.setEmail(users.getEmail());
        user.setFullName(users.getFullName());

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
