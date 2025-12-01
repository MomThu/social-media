package com.example.post.model;

import lombok.*;

import java.time.Instant;

import com.example.common.model.Auditable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends Auditable {
    private String userId;
    private String text;
    private Instant createdAt;
}
