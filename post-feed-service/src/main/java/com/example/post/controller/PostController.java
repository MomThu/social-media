package com.example.post.controller;

import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.common.web.APIResponse;
import com.example.post.dto.PostCommentDto;
import com.example.post.dto.PostRequestDto;
import com.example.post.dto.PostResponseDto;
import com.example.post.dto.SearchPostRequestDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Post Feed Service")
@RequestMapping("/api/posts")
public interface PostController {
    // create post
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<PostResponseDto> create(@Valid @RequestBody PostRequestDto req);

    // search posts
    @PostMapping(path = "/findData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<HashMap<String, Object>> findData(@Valid @RequestBody SearchPostRequestDto req);

    // get post by id
    @GetMapping("/{id}")
    APIResponse<PostResponseDto> getById(@PathVariable String id);

    // update post
    @PostMapping(path = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<PostResponseDto> update(@PathVariable String id, @Valid @RequestBody PostRequestDto req);

    // delete post
    @PostMapping(path = "/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<Void> delete(@PathVariable String id);

    // like function
    @PostMapping(path = "/{id}/like", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)  
    APIResponse<Void> like(@PathVariable String id);

    // comment function
    @PostMapping(path = "/{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<String> comment(@PathVariable String id, @Valid @RequestBody PostCommentDto req);
}