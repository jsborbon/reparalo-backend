package com.reparalo.commentservice.controller;

import com.reparalo.commentservice.dto.CommentDTO;
import com.reparalo.commentservice.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        String userName = principal.getToken().getClaimAsString("name");
        CommentDTO createdComment = commentService.createComment(commentDTO, userId, userName);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable String id) throws ExecutionException, InterruptedException {
        try {
            CommentDTO commentDTO = commentService.getCommentById(id);
            return ResponseEntity.ok(commentDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTarget(
            @PathVariable String targetId,
            @RequestParam String targetType) throws ExecutionException, InterruptedException {
        List<CommentDTO> comments = commentService.getCommentsByTargetIdAndType(targetId, targetType);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CommentDTO>> getCommentsByUser(
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable String id,
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        try {
            CommentDTO updatedComment = commentService.updateComment(id, commentDTO, userId);
            return ResponseEntity.ok(updatedComment);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        try {
            commentService.deleteComment(id, userId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}