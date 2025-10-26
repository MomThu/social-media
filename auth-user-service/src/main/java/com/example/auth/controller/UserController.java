package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) { this.repo = repo; }

    @GetMapping("/me")
    public Optional<User> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return Optional.empty();
        return repo.findByUsername(principal.getUsername());
    }

    @PutMapping("/me")
    public User update(@AuthenticationPrincipal UserDetails principal, @RequestBody User update) {
        User u = repo.findByUsername(principal.getUsername()).orElseThrow();
        u.setBio(update.getBio());
        u.setAvatarUrl(update.getAvatarUrl());
        u.setSkills(update.getSkills());
        return repo.save(u);
    }
}
