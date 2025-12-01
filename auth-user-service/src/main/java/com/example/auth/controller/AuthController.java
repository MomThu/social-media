package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository repo,
                          BCryptPasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (repo.existsByUsername(req.getUsername()) || repo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Username or email already exists");
        }
        User u = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .build();
        repo.save(u);
        String token = jwtUtil.generateToken(u.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.example.auth.dto.AuthRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            String token = jwtUtil.generateToken(req.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
