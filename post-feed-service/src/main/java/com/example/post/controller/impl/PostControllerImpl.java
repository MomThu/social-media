package com.example.post.controller.impl;

import java.util.HashMap;

import org.springframework.web.bind.annotation.RestController;

import com.example.common.web.APIResponse;
import com.example.post.controller.PostController;
import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
import com.example.post.model.Comment;
import com.example.post.service.PostService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PostControllerImpl implements PostController {
    private final PostService postService;

    public PostControllerImpl(PostService postService) {
        this.postService = postService;
    }

    @Override
    public APIResponse<PostResponseDto> create(PostRequestDto req) {
        log.info("create: " + req);
        PostResponseDto dto = postService.create(req);
        return APIResponse.ok(null, dto, null);
    }

    @Override
    public APIResponse<HashMap<String, Object>> findData(SearchPostRequestDto req) {
        log.info("findData: " + req);
        HashMap<String, Object> dto = postService.findData(req);
        return APIResponse.ok(null, dto, null);
    }

    @Override
    public APIResponse<PostResponseDto> getById(String id) {
        log.info("getById: " + id);
        PostResponseDto dto = postService.getById(id);
        return APIResponse.ok(null, dto, null);
    }


    @Override
    public APIResponse<PostResponseDto> update(String id, PostRequestDto req) {
        log.info("update: " + id + ", " + req);
        PostResponseDto dto = postService.update(id, req);
        return APIResponse.ok(null, dto, null);
    }

    @Override
    public APIResponse<Void> delete(String id) {
        log.info("delete: " + id);
        postService.delete(id);
        return APIResponse.ok(null, null, null);
    }

    @Override
    public APIResponse<String> comment(String id, PostCommentDto req) {
        log.info("comment: " + id + ", " + req);
        String commentId = postService.comment(id, req);
        return APIResponse.ok(null, commentId, null);
    }

    @Override
    public APIResponse<Void> like(String id) {
        log.info("like: " + id);
        postService.like(id);
        return APIResponse.ok(null, null, null);
    }

}
