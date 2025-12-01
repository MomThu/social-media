package com.example.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDto {
    private Long id;
    private Long authorId;
    private String caption;
    private String[] mediaUrls;
    private int likes;
    private String[] comments;
    private String authorName;
    private String requestId;
}
