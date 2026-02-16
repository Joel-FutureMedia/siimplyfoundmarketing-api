package com.simplyfound.emailmarketapi.Services;

import com.simplyfound.emailmarketapi.Models.User;
import com.simplyfound.emailmarketapi.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Map<String, Object> login(String email, String password) {
        log.info("Login attempt for email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid password for email: {}", email);
            throw new RuntimeException("Invalid email or password");
        }

        log.info("Login successful for email: {}", email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("user", Map.of(
            "id", user.getId(),
            "email", user.getEmail()
        ));
        
        return response;
    }

    @Transactional
    public Map<String, Object> register(String email, String password) {
        log.info("Registration attempt for email: {}", email);
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        
        User saved = userRepository.save(user);
        log.info("User registered successfully: {}", saved.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registration successful");
        response.put("user", Map.of(
            "id", saved.getId(),
            "email", saved.getEmail()
        ));
        
        return response;
    }

    public boolean verifyUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }
}


