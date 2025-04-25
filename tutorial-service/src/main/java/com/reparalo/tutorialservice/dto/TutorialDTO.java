package com.reparalo.tutorialservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialDTO {
    private String id;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;
    
    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @NotBlank(message = "La dificultad es obligatoria")
    private String dificultad; // FACIL, INTERMEDIO, AVANZADO
    
    private List<String> imagenes;
    private String videoUrl;
    private Map<String, Object> metadatos;
}