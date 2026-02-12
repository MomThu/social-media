package com.example.media.service.impl;

import com.example.media.model.Media;
import com.example.media.repository.MediaRepository;
import com.example.media.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final Path uploadDir = Paths.get("uploads");

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
        }
    }

    @Override
    public Media uploadMedia(MultipartFile file, String ownerId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(original);
        String name = Instant.now().toEpochMilli() + "-" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path target = uploadDir.resolve(name);
        
        Files.copy(file.getInputStream(), target);
        
        Media media = Media.builder()
                .filename(name)
                .url("/uploads/" + name)
                .ownerId(ownerId)
                .createdAt(Instant.now())
                .build();
        
        return mediaRepository.save(media);
    }

    @Override
    public Optional<Media> getMedia(String id) {
        return mediaRepository.findById(id);
    }

    @Override
    public Resource downloadMedia(String id) throws IOException {
        Optional<Media> mediaOpt = mediaRepository.findById(id);
        if (mediaOpt.isEmpty()) {
            throw new IllegalArgumentException("Media not found with id: " + id);
        }
        
        Media media = mediaOpt.get();
        Path filePath = uploadDir.resolve(media.getFilename());
        
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + media.getFilename());
        }
        
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Could not read file: " + media.getFilename());
        }
    }

    @Override
    public void deleteMedia(String id) throws IOException {
        Optional<Media> mediaOpt = mediaRepository.findById(id);
        if (mediaOpt.isEmpty()) {
            throw new IllegalArgumentException("Media not found with id: " + id);
        }
        
        Media media = mediaOpt.get();
        Path filePath = uploadDir.resolve(media.getFilename());
        
        // Delete file from filesystem
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // Delete from database
        mediaRepository.deleteById(id);
    }
}
