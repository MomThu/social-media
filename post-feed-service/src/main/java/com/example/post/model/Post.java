package com.example.post.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.common.model.Auditable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends Auditable {
    @Id
    private String id;
    private String authorId;
    private String caption;
    private List<String> mediaUrls;
    private int likes;
    private List<Comment> comments;
}
