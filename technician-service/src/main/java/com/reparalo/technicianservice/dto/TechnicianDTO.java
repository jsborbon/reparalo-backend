package com.reparalo.technicianservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDTO {
    private String id;
    
    private String userId;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String description;
    
    private String profilePictureUrl;
    
    private List<String> specialties;
    
    @NotBlank(message = "La ubicación no puede estar vacía")
    private String location;
    
    private Double rating;
    
    private Integer reviewCount;
    
    private Boolean available;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}