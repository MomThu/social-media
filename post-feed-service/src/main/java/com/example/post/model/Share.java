package com.example.post.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Share {
    private String userId;
    private Instant sharedAt;
    private String sharedTo; // could be "feed", "private_message", etc.
}
