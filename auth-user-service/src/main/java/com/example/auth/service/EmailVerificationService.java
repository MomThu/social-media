package com.example.auth.service;

import com.example.auth.model.EmailVerificationToken;
import com.example.auth.model.User;
import com.example.auth.repository.EmailVerificationTokenRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.email-verification.token-expiry-hours:24}")
    private int tokenExpiryHours;

    @Transactional
    public void sendVerificationEmail(User user) {
        // Invalidate previous tokens
        tokenRepository.findByUserAndVerifiedFalse(user).ifPresent(token -> {
            token.setVerified(true); // Mark as invalid by setting verified = true
            tokenRepository.save(token);
        });

        // Generate new token
        String verificationToken = UUID.randomUUID().toString();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(verificationToken)
                .user(user)
                .expiryDate(Instant.now().plus(tokenExpiryHours, ChronoUnit.HOURS))
                .verified(false)
                .build();

        tokenRepository.save(token);
        emailService.sendEmailVerificationEmail(user.getEmail(), verificationToken);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Transactional
    public void verifyEmail(String token) {
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid verification token");
        }

        EmailVerificationToken verificationToken = tokenOpt.get();

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        if (verificationToken.isVerified()) {
            throw new IllegalArgumentException("Email has already been verified");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setVerified(true);
        tokenRepository.save(verificationToken);

        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        log.info("Email verified for user: {}", user.getEmail());
    }
}
