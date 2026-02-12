package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import com.example.common.web.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/success")
    public APIResponse<Map<String, Object>> oauth2LoginSuccess(Authentication authentication) {
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String provider = authentication.getName(); // google or facebook

            log.info("OAuth2 login successful for user: {} via {}", email, provider);

            // Find or create user
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                // Create new user from OAuth2
                user = User.builder()
                        .email(email)
                        .username(name)
                        .password("") // OAuth2 users don't have password
                        .emailVerified(true) // OAuth2 emails are pre-verified
                        .oauthProvider(provider)
                        .build();
                userRepository.save(user);
                log.info("New user created via OAuth2: {}", email);
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "username", user.getUsername()
            ));

            return APIResponse.ok(
                "OAuth2 login successful",
                response
            );
        } catch (Exception e) {
            log.error("OAuth2 login failed", e);
            return APIResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "OAuth2 login failed: " + e.getMessage()
            );
        }
    }

    @PostMapping("/google/callback")
    public APIResponse<Map<String, Object>> googleCallback(@RequestParam String code) {
        // This endpoint receives the OAuth2 authorization code from Google
        // In production, exchange the code for tokens using Google's token endpoint
        log.info("Google OAuth2 callback received with code: {}", code);
        return APIResponse.ok(
            "Google callback received",
            null
        );
    }

    @PostMapping("/facebook/callback")
    public APIResponse<Map<String, Object>> facebookCallback(@RequestParam String code) {
        // This endpoint receives the OAuth2 authorization code from Facebook
        // In production, exchange the code for tokens using Facebook's token endpoint
        log.info("Facebook OAuth2 callback received with code: {}", code);
        return APIResponse.ok(
            "Facebook callback received",
            null
        );
    }
}
