package com.example.auth.service;

import com.example.auth.model.PasswordResetToken;
import com.example.auth.model.User;
import com.example.auth.repository.PasswordResetTokenRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.token-expiry-hours:1}")
    private int tokenExpiryHours;

    @Transactional
    public void requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Invalidate previous tokens
            tokenRepository.findByUserAndUsedFalse(user).ifPresent(token -> {
                token.setUsed(true);
                tokenRepository.save(token);
            });

            // Generate new token
            String resetToken = UUID.randomUUID().toString();
            PasswordResetToken token = PasswordResetToken.builder()
                    .token(resetToken)
                    .user(user)
                    .expiryDate(Instant.now().plus(tokenExpiryHours, ChronoUnit.HOURS))
                    .used(false)
                    .build();

            tokenRepository.save(token);
            emailService.sendPasswordResetEmail(email, resetToken);
            log.info("Password reset requested for user: {}", email);
        } else {
            log.warn("Password reset requested for non-existent email: {}", email);
            // Don't reveal if email exists or not for security
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid reset token");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Reset token has already been used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getEmail());
    }
}
