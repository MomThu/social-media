package com.example.media.service;

import com.example.media.model.Media;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface MediaService {
    Media uploadMedia(MultipartFile file, String ownerId) throws IOException;
    Optional<Media> getMedia(String id);
    Resource downloadMedia(String id) throws IOException;
    void deleteMedia(String id) throws IOException;
}
