package com.example.post.controller;

import com.example.post.model.Comment;
import com.example.post.model.Post;
import com.example.post.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository repo;

    public PostController(PostRepository repo) { this.repo = repo; }

    @PostMapping
    public Post create(@RequestBody Post p) {
        p.setCreatedAt(Instant.now());
        p.setLikes(0);
        return repo.save(p);
    }

    @GetMapping
    public List<Post> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@PathVariable String id) {
        return repo.findById(id).map(p -> {
            p.setLikes(p.getLikes() + 1);
            repo.save(p);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> comment(@PathVariable String id, @RequestBody Comment c) {
        return repo.findById(id).map(p -> {
            if (p.getComments() == null) p.setComments(new java.util.ArrayList<>());
            c.setCreatedAt(Instant.now());
            p.getComments().add(c);
            repo.save(p);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}