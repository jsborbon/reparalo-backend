package com.reparalo.commentservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String id;
    
    private String userId;
    
    private String userName;
    
    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(min = 3, max = 500, message = "El contenido debe tener entre 3 y 500 caracteres")
    private String content;
    
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;
    
    @NotBlank(message = "El ID del objetivo no puede estar vacío")
    private String targetId;
    
    @NotBlank(message = "El tipo de objetivo no puede estar vacío")
    private String targetType;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}