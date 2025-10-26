package com.example.post.model;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private String userId;
    private String text;
    private Instant createdAt;
}
