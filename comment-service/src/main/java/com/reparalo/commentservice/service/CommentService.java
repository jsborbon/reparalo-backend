package com.reparalo.commentservice.service;

import com.reparalo.commentservice.dto.CommentDTO;
import com.reparalo.commentservice.model.Comment;
import com.reparalo.commentservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentDTO createComment(CommentDTO commentDTO, String userId, String userName) throws ExecutionException, InterruptedException {
        Comment comment = mapToEntity(commentDTO);
        comment.setUserId(userId);
        comment.setUserName(userName);
        
        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    public CommentDTO getCommentById(String id) throws ExecutionException, InterruptedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comentario no encontrado con ID: " + id));
        return mapToDTO(comment);
    }

    public List<CommentDTO> getCommentsByTargetIdAndType(String targetId, String targetType) throws ExecutionException, InterruptedException {
        List<Comment> comments = commentRepository.findByTargetIdAndTargetType(targetId, targetType);
        return comments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByUserId(String userId) throws ExecutionException, InterruptedException {
        List<Comment> comments = commentRepository.findByUserId(userId);
        return comments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO updateComment(String id, CommentDTO commentDTO, String userId) throws ExecutionException, InterruptedException {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comentario no encontrado con ID: " + id));
        
        // Verificar que el usuario es el autor del comentario
        if (!existingComment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para editar este comentario");
        }
        
        existingComment.setContent(commentDTO.getContent());
        existingComment.setRating(commentDTO.getRating());
        
        Comment updatedComment = commentRepository.save(existingComment);
        return mapToDTO(updatedComment);
    }

    public void deleteComment(String id, String userId) throws ExecutionException, InterruptedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comentario no encontrado con ID: " + id));
        
        // Verificar que el usuario es el autor del comentario
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar este comentario");
        }
        
        commentRepository.delete(id);
    }

    private Comment mapToEntity(CommentDTO commentDTO) {
        return Comment.builder()
                .id(commentDTO.getId())
                .userId(commentDTO.getUserId())
                .userName(commentDTO.getUserName())
                .content(commentDTO.getContent())
                .rating(commentDTO.getRating())
                .targetId(commentDTO.getTargetId())
                .targetType(commentDTO.getTargetType())
                .createdAt(commentDTO.getCreatedAt())
                .updatedAt(commentDTO.getUpdatedAt())
                .build();
    }

    private CommentDTO mapToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .userName(comment.getUserName())
                .content(comment.getContent())
                .rating(comment.getRating())
                .targetId(comment.getTargetId())
                .targetType(comment.getTargetType())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}