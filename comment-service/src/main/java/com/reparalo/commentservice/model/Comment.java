package com.reparalo.commentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;
    private String userId;
    private String userName;
    private String content;
    private Integer rating;
    private String targetId; // ID del tutorial, técnico u otro elemento al que se refiere
    private String targetType; // Tipo de elemento (tutorial, técnico, etc.)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}