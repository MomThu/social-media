package com.example.post.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    private String id;
    private String authorId;
    private String caption;
    private List<String> mediaUrls;
    private int likes;
    private List<Comment> comments;
    private Instant createdAt;
}
