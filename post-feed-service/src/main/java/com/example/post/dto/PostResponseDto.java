package com.example.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private String id;
    private String authorId;
    private String caption;
    private List<String> mediaUrls;
    private int likes;
    private List<CommentDto> comments;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDto {
        private String userId;
        private String text;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
