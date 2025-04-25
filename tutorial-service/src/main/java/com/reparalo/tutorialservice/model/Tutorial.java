package com.reparalo.tutorialservice.model;

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
public class Tutorial {
    private String id;
    private String titulo;
    private String descripcion;
    private String contenido;
    private String categoria; // Categoría del tutorial (Plomería, Electricidad, etc.)
    private String dificultad; // FACIL, INTERMEDIO, AVANZADO
    private String autorId; // UID del técnico o admin que creó el tutorial
    private String autorNombre; // Nombre del autor para mostrar
    private List<String> imagenes; // URLs de las imágenes
    private String videoUrl; // URL del video si existe
    private Map<String, Object> metadatos; // Datos adicionales
    private int vistas;
    private int likes;
    private boolean activo;
    private String fechaCreacion;
    private String fechaActualizacion;
}