package com.example.springredditclone.controller;

import com.example.springredditclone.dto.CommentsDto;
import com.example.springredditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {
        commentService.save(commentsDto);
        return new ResponseEntity<>(CREATED);
    }
    // Api các comment theo id bài Post (Read Post)
    @GetMapping("by-post/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId) {
        return status(OK).body(commentService.getCommentByPost(postId));
    }
    // Api trả về tất cả comment của User này (Profile)
    @GetMapping("by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByUser(@PathVariable String userName) {
        return status(OK).body(commentService.getCommentsByUser(userName));
    }
}