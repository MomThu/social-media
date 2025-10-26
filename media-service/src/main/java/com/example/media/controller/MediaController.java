package com.example.media.controller;

import com.example.media.model.Media;
import com.example.media.repository.MediaRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaRepository repo;
    private final Path uploadDir = Paths.get("uploads");

    public MediaController(MediaRepository repo) {
        this.repo = repo;
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ignored) {}
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "ownerId", required = false) String ownerId) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("file empty");
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(original);
        String name = Instant.now().toEpochMilli() + "-" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path target = uploadDir.resolve(name);
        try {
            Files.copy(file.getInputStream(), target);
            Media m = Media.builder()
                    .filename(name)
                    .url("/uploads/" + name)
                    .ownerId(ownerId)
                    .createdAt(Instant.now())
                    .build();
            Media saved = repo.save(m);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", saved.getId());
            resp.put("url", saved.getUrl());
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error saving file");
        }
    }
}