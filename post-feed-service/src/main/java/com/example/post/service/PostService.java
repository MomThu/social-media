package com.example.post.service;

import java.util.HashMap;
import java.util.List;

import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;
import com.example.post.dto.FeedRequestDto;

public interface PostService {
    PostResponseDto create(PostRequestDto req);

    HashMap<String, Object> findData(SearchPostRequestDto req);

    PostResponseDto getById(String id);

    PostResponseDto update(String id, PostRequestDto req);

    void delete(String id);

    String comment(String id, PostCommentDto c);

    void like(String id, String userId);

    void unlike(String id, String userId);

    void share(String id, String userId, String sharedTo);

    List<PostResponseDto> getPersonalizedFeed(FeedRequestDto req);
}
