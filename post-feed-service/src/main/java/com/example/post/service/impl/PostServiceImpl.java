package com.example.post.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
import com.example.post.dto.FeedRequestDto;
import com.example.post.model.Post;
import com.example.post.model.Comment;
import com.example.post.model.Share;
import com.example.post.repository.PostRepository;
import com.example.post.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    // validate data
    private void validatePostRequest(PostRequestDto req) {
        if (req.getCaption() == null || req.getCaption().isEmpty()) {
            throw new IllegalArgumentException("Caption cannot be null or empty");
        }
        if (req.getMediaUrls() == null || req.getMediaUrls().length == 0) {
            throw new IllegalArgumentException("Media URLs cannot be null or empty");
        }
    }

    // Convert Post to PostResponseDto
    private PostResponseDto convertToResponseDto(Post post, String currentUserId) {
        boolean likedByCurrentUser = post.getLikedByUserIds() != null && 
                                     post.getLikedByUserIds().contains(currentUserId);
        
        List<PostCommentDto> commentDtos = post.getComments() != null ? 
            post.getComments().stream()
                .map(c -> PostCommentDto.builder()
                    .id(c.getId())
                    .userId(c.getUserId())
                    .text(c.getText())
                    .createdAt(c.getCreatedAt())
                    .updatedAt(c.getUpdatedAt())
                    .build())
                .collect(Collectors.toList()) : new ArrayList<>();

        return PostResponseDto.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .caption(post.getCaption())
                .mediaUrls(post.getMediaUrls())
                .likes(post.getLikes())
                .comments(commentDtos)
                .shareCount(post.getShareCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likedByCurrentUser(likedByCurrentUser)
                .build();
    }

    @Override
    public PostResponseDto getById(String id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            return convertToResponseDto(postOpt.get(), null);
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
            Post updated = postRepository.save(post);
            return convertToResponseDto(updated, req.getAuthorId());
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
            .id(UUID.randomUUID().toString())
            .authorId(req.getAuthorId())
            .authorName(req.getAuthorName())
            .caption(req.getCaption())
            .mediaUrls(java.util.Arrays.asList(req.getMediaUrls()))
            .likes(0)
            .likedByUserIds(new ArrayList<>())
            .comments(new ArrayList<>())
            .shares(new ArrayList<>())
            .shareCount(0)
            .build();
        Post saved = postRepository.save(post);
        return convertToResponseDto(saved, req.getAuthorId());
    }

    @Override
    public HashMap<String, Object> findData(SearchPostRequestDto req) {
        // Simple search/pagination implementation
        List<Post> posts = postRepository.findAll();
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", posts.size());
        result.put("posts", posts.stream()
            .map(p -> convertToResponseDto(p, null))
            .collect(Collectors.toList()));
        return result;
    }

    @Override
    public String comment(String id, PostCommentDto c) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (post.getComments() == null) {
                post.setComments(new ArrayList<>());
            }
            
            String commentId = UUID.randomUUID().toString();
            Comment comment = Comment.builder()
                    .userId(c.getUserId())
                    .text(c.getText())
                    .build();
            // Set createdAt and id manually as they're not set by builder
            comment.setCreatedAt(Instant.now());
            
            post.getComments().add(comment);
            postRepository.save(post);
            return commentId;
        }
        return null;
    }

    @Override
    public void like(String id, String userId) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            
            // Initialize likedByUserIds if null
            if (post.getLikedByUserIds() == null) {
                post.setLikedByUserIds(new ArrayList<>());
            }
            
            // Prevent duplicate likes
            if (!post.getLikedByUserIds().contains(userId)) {
                post.getLikedByUserIds().add(userId);
                post.setLikes(post.getLikes() + 1);
                postRepository.save(post);
                log.info("User " + userId + " liked post " + id);
            }
        }
    }

    @Override
    public void unlike(String id, String userId) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            
            if (post.getLikedByUserIds() != null && post.getLikedByUserIds().contains(userId)) {
                post.getLikedByUserIds().remove(userId);
                post.setLikes(Math.max(0, post.getLikes() - 1));
                postRepository.save(post);
                log.info("User " + userId + " unliked post " + id);
            }
        }
    }

    @Override
    public void share(String id, String userId, String sharedTo) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            
            if (post.getShares() == null) {
                post.setShares(new ArrayList<>());
            }
            
            Share share = Share.builder()
                    .userId(userId)
                    .sharedAt(Instant.now())
                    .sharedTo(sharedTo)
                    .build();
            
            post.getShares().add(share);
            post.setShareCount(post.getShareCount() + 1);
            postRepository.save(post);
            log.info("User " + userId + " shared post " + id + " to " + sharedTo);
        }
    }

    @Override
    public List<PostResponseDto> getPersonalizedFeed(FeedRequestDto req) {
        // Personalized feed: 
        // 1. Get all posts
        // 2. Sort by creation date (newest first)
        // 3. Apply pagination
        // 4. Boost posts with more engagement (likes, comments, shares)
        
        List<Post> allPosts = postRepository.findAll();
        
        // Sort by engagement score (likes + comments + shares) and creation date
        List<Post> sortedPosts = allPosts.stream()
            .sorted((p1, p2) -> {
                int score1 = p1.getLikes() + (p1.getComments() != null ? p1.getComments().size() : 0) + p1.getShareCount();
                int score2 = p2.getLikes() + (p2.getComments() != null ? p2.getComments().size() : 0) + p2.getShareCount();
                
                if (score1 != score2) {
                    return Integer.compare(score2, score1); // Higher engagement first
                }
                // If engagement is same, sort by creation date
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            })
            .collect(Collectors.toList());
        
        // Apply pagination
        int page = req.getPage() > 0 ? req.getPage() : 0;
        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, sortedPosts.size());
        
        if (start >= sortedPosts.size()) {
            return new ArrayList<>();
        }
        
        return sortedPosts.subList(start, end).stream()
            .map(p -> convertToResponseDto(p, req.getUserId()))
            .collect(Collectors.toList());
    }
}

