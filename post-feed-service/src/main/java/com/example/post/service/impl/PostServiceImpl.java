package com.example.post.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
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

    // validate data
    private void validatePostRequest(PostRequestDto req) {
        if (req.getCaption() == null || req.getCaption().isEmpty()) {
            throw new IllegalArgumentException("Caption cannot be null or empty");
        }
        if (req.getMediaUrls() == null || req.getMediaUrls().length == 0) {
            throw new IllegalArgumentException("Media URLs cannot be null or empty");
        }
    }

    @Override
    public PostResponseDto getById(String id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            // TODO: convert Post to PostResponseDto
            return new PostResponseDto();
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
            postRepository.save(post);
            // TODO: convert Post to PostResponseDto
            return new PostResponseDto();
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
            // .authorId(req.getAuthorId())
            .caption(req.getCaption())
            .mediaUrls(java.util.Arrays.asList(req.getMediaUrls()))
            .likes(req.getLikes())
            .comments(new ArrayList<>())
            .build();
        postRepository.save(post);
        // TODO: convert Post to PostResponseDto
        return new PostResponseDto();
    }

    @Override
    public HashMap<String, Object> findData(SearchPostRequestDto req) {
        // TODO: implement search logic
        return new HashMap<>();
    }

    @Override
    public String comment(String id, PostCommentDto c) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (post.getComments() == null) {
                post.setComments(new ArrayList<>());
            }
            // post.getComments().add(c);
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
