package com.example.post.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.common.model.Auditable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Post extends Auditable {
    @Id
    private String id;
    private String authorId;
    private String authorName;
    private String caption;
    private List<String> mediaUrls;
    private List<String> likedByUserIds; // track who liked the post
    private int likes;
    private List<Comment> comments;
    private List<Share> shares; // track shares
    private int shareCount;
}
