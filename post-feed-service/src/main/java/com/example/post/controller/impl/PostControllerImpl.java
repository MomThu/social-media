package com.example.post.controller.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.example.common.web.APIResponse;
import com.example.post.controller.PostController;
import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
import com.example.post.dto.FeedRequestDto;
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
    public APIResponse<Void> like(String id, String userId) {
        log.info("like: " + id + " by user: " + userId);
        postService.like(id, userId);
        return APIResponse.ok(null, null, null);
    }

    @Override
    public APIResponse<Void> unlike(String id, String userId) {
        log.info("unlike: " + id + " by user: " + userId);
        postService.unlike(id, userId);
        return APIResponse.ok(null, null, null);
    }

    @Override
    public APIResponse<Void> share(String id, String userId, String sharedTo) {
        log.info("share: " + id + " by user: " + userId + " to: " + sharedTo);
        postService.share(id, userId, sharedTo);
        return APIResponse.ok(null, null, null);
    }

    @Override
    public APIResponse<List<PostResponseDto>> getPersonalizedFeed(FeedRequestDto req) {
        log.info("getPersonalizedFeed: userId=" + req.getUserId() + ", page=" + req.getPage() + ", pageSize=" + req.getPageSize());
        List<PostResponseDto> feed = postService.getPersonalizedFeed(req);
        return APIResponse.ok(null, feed, null);
    }
}

