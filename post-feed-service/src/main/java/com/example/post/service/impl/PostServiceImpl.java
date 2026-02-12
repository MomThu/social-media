package com.example.post.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
import com.example.post.model.Comment;
import com.example.post.model.Post;
import com.example.post.repository.PostRepository;
import com.example.post.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    // validate data
    private void validatePostRequest(PostRequestDto req) {
        if (req.getCaption() == null || req.getCaption().isEmpty()) {
            throw new IllegalArgumentException("Caption cannot be null or empty");
        }
        if (req.getMediaUrls() == null || req.getMediaUrls().length == 0) {
            throw new IllegalArgumentException("Media URLs cannot be null or empty");
        }
    }

    // Mapper method to convert Post to PostResponseDto
    private PostResponseDto mapToResponseDto(Post post) {
        if (post == null) {
            return null;
        }
        
        List<PostResponseDto.CommentDto> commentDtos = new ArrayList<>();
        if (post.getComments() != null) {
            commentDtos = post.getComments().stream()
                .map(comment -> PostResponseDto.CommentDto.builder()
                    .userId(comment.getUserId())
                    .text(comment.getText())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build())
                .collect(Collectors.toList());
        }
        
        return PostResponseDto.builder()
            .id(post.getId())
            .authorId(post.getAuthorId())
            .caption(post.getCaption())
            .mediaUrls(post.getMediaUrls())
            .likes(post.getLikes())
            .comments(commentDtos)
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .createdBy(post.getCreatedBy())
            .updatedBy(post.getUpdatedBy())
            .build();
    }

    @Override
    public PostResponseDto getById(String id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            return mapToResponseDto(post);
        }
        return null;
    }

    @Override
    public PostResponseDto update(String id, PostRequestDto req) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setCaption(req.getCaption());
            post.setMediaUrls(java.util.Arrays.asList(req.getMediaUrls()));
            Post savedPost = postRepository.save(post);
            return mapToResponseDto(savedPost);
        }
        return null;
    }

    @Override
    public void delete(String id) {
        postRepository.deleteById(id);
    }

    @Override
    public PostResponseDto create(PostRequestDto req) {
        validatePostRequest(req);
        Post post = Post.builder()
            .authorId(req.getAuthorId() != null ? req.getAuthorId().toString() : null)
            .caption(req.getCaption())
            .mediaUrls(java.util.Arrays.asList(req.getMediaUrls()))
            .likes(req.getLikes())
            .comments(new ArrayList<>())
            .build();
        Post savedPost = postRepository.save(post);
        return mapToResponseDto(savedPost);
    }

    @Override
    public HashMap<String, Object> findData(SearchPostRequestDto req) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        
        // Filter by authorId if provided
        if (req.getAuthorId() != null) {
            criteria.add(Criteria.where("authorId").is(req.getAuthorId().toString()));
        }
        
        // Search caption if provided
        if (req.getCaption() != null && !req.getCaption().isEmpty()) {
            criteria.add(Criteria.where("caption").regex(req.getCaption(), "i"));
        }
        
        // Filter by authorName if provided (this assumes authorName might be stored or used differently)
        if (req.getAuthorName() != null && !req.getAuthorName().isEmpty()) {
            criteria.add(Criteria.where("authorName").regex(req.getAuthorName(), "i"));
        }
        
        // Apply all criteria
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        // Add sorting by creation date (newest first)
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Get total count
        long total = mongoTemplate.count(query, Post.class);
        
        // Apply pagination (default: page 0, size 10)
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        query.with(pageable);
        
        // Execute query
        List<Post> posts = mongoTemplate.find(query, Post.class);
        
        // Convert to DTOs
        List<PostResponseDto> postDtos = posts.stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
        
        // Build response
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", postDtos);
        response.put("total", total);
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", (total + size - 1) / size);
        
        return response;
    }

    @Override
    public String comment(String id, PostCommentDto c) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (post.getComments() == null) {
                post.setComments(new ArrayList<>());
            }
            // Convert PostCommentDto to Comment model
            Comment comment = Comment.builder()
                .userId(c.getUserId())
                .text(c.getText())
                .build();
            post.getComments().add(comment);
            postRepository.save(post);
            return c.getId();
        }
        return null;
    }

    @Override
    public void like(String id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setLikes(post.getLikes() + 1);
            postRepository.save(post);
        }
    }
}
