package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.service.PasswordResetService;
import com.example.auth.service.EmailVerificationService;
import com.example.common.web.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/password-reset/request")
    public APIResponse<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto dto) {
        log.info("Password reset requested for email: {}", dto.getEmail());
        passwordResetService.requestPasswordReset(dto.getEmail());
        return APIResponse.ok(
            "If an account exists with this email, a password reset link has been sent.",
            null
        );
    }

    @PostMapping("/password-reset/reset")
    public APIResponse<Void> resetPassword(@Valid @RequestBody PasswordResetDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return APIResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Passwords do not match"
            );
        }

        try {
            passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());
            return APIResponse.ok(
                "Password has been reset successfully",
                null
            );
        } catch (IllegalArgumentException e) {
            return APIResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
        }
    }

    @PostMapping("/email/verify")
    public APIResponse<Void> verifyEmail(@Valid @RequestBody EmailVerificationDto dto) {
        try {
            emailVerificationService.verifyEmail(dto.getToken());
            return APIResponse.ok(
                "Email has been verified successfully",
                null
            );
        } catch (IllegalArgumentException e) {
            return APIResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
        }
    }

    @PostMapping("/email/resend-verification")
    public APIResponse<Void> resendVerificationEmail(@Valid @RequestBody PasswordResetRequestDto dto) {
        // This will be implemented in auth service to send verification email for existing user
        return APIResponse.ok(
            "Verification email has been sent",
            null
        );
    }
}
