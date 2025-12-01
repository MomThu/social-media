package com.example.post.service.impl;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public PostResponseDto update(String id, PostRequestDto req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }


    @Override
    public PostResponseDto create(PostRequestDto req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }


    @Override
    public HashMap<String, Object> findData(SearchPostRequestDto req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findData'");
    }


    @Override
    public String comment(String id, PostCommentDto c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'comment'");
    }


    @Override
    public void like(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'like'");
    }
}
