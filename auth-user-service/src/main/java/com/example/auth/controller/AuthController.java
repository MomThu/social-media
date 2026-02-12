package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import com.example.auth.service.EmailVerificationService;
import com.example.common.web.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public APIResponse<?> register(@RequestBody RegisterRequest req) {
        if (repo.existsByUsername(req.getUsername()) || repo.existsByEmail(req.getEmail())) {
            return APIResponse.error(
                HttpStatus.CONFLICT.value(),
                "Username or email already exists"
            );
        }
        
        User u = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .emailVerified(false)
                .build();
        
        repo.save(u);
        
        // Send verification email
        emailVerificationService.sendVerificationEmail(u);
        
        log.info("New user registered: {}", req.getEmail());
        
        return APIResponse.ok(
            "Registration successful. Please check your email to verify your account.",
            Map.of("message", "Verification email sent")
        );
    }

    @PostMapping("/login")
    public APIResponse<?> login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            
            User user = repo.findByUsername(req.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            if (!user.isEmailVerified()) {
                return APIResponse.error(
                    HttpStatus.FORBIDDEN.value(),
                    "Please verify your email before logging in"
                );
            }
            
            String token = jwtUtil.generateToken(req.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "username", user.getUsername()
            ));
            
            return APIResponse.ok(
                "Login successful",
                response
            );
        } catch (AuthenticationException ex) {
            log.warn("Login failed for user: {}", req.getUsername());
            return APIResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid credentials"
            );
        }
    }
}
