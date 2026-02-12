package com.example.media.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.media.model.Media;
import com.example.media.service.MediaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "ownerId", required = false) String ownerId) {
        try {
            Media saved = mediaService.uploadMedia(file, ownerId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", saved.getId());
            resp.put("url", saved.getUrl());
            resp.put("filename", saved.getFilename());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable String id) {
        return mediaService.getMedia(id)
                .map(media -> {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("id", media.getId());
                    resp.put("filename", media.getFilename());
                    resp.put("url", media.getUrl());
                    resp.put("ownerId", media.getOwnerId());
                    resp.put("createdAt", media.getCreatedAt());
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadMedia(@PathVariable String id) {
        try {
            Resource resource = mediaService.downloadMedia(id);
            Media media = mediaService.getMedia(id).orElseThrow();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + media.getFilename() + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading file");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedia(@PathVariable String id) {
        try {
            mediaService.deleteMedia(id);
            return ResponseEntity.ok().body("Media deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file");
        }
    }
}